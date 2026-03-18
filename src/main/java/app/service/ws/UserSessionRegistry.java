package app.service.ws;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class UserSessionRegistry {
    private final ConcurrentMap<String, Set<String>> usernameToSessionSet = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> sessionIdToUsername = new ConcurrentHashMap<>();

    public Set<String> getSessionSet(String username) {
        return usernameToSessionSet.computeIfAbsent(username, key -> ConcurrentHashMap.newKeySet());
    }

    public void removeSessionForUser(String sessionId, String username) {
        getSessionSet(username).remove(sessionId);
        sessionIdToUsername.remove(sessionId, username);
    }

    public void addSessionForUser(String sessionId, String username) {
        getSessionSet(username).add(sessionId);
        sessionIdToUsername.put(sessionId, username);
    }

    public Optional<String> removeSession(String sessionId) {
        if (sessionId == null) return Optional.empty();
        String username = sessionIdToUsername.remove(sessionId);
        if (username != null) {
            getSessionSet(username).remove(sessionId);
        }
        return Optional.ofNullable(username);
    }

    public int getActiveSessionCount() {
        return usernameToSessionSet.values().stream().mapToInt(Set::size).sum();
    }

    public int getActiveUserCount() {
        return (int) usernameToSessionSet.values().stream().filter(s -> !s.isEmpty()).count();
    }
}
