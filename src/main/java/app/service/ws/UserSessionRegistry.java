package app.service.ws;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class UserSessionRegistry {
    private final ConcurrentMap<String, Set<String>> usernameToSessionMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> sessionIdToUsernameMap = new ConcurrentHashMap<>();

    public Set<String> getSessionSet(String username) {
        return usernameToSessionMap.computeIfAbsent(username, key -> ConcurrentHashMap.newKeySet());
    }

    public void addSessionForUser(String sessionId, String username) {
        getSessionSet(username).add(sessionId);
        sessionIdToUsernameMap.put(sessionId, username);
    }

    public Optional<String> removeSession(String sessionId) {
        if (sessionId == null) return Optional.empty();
        String username = sessionIdToUsernameMap.remove(sessionId);
        if (username != null) {
            getSessionSet(username).remove(sessionId);
        }
        return Optional.ofNullable(username);
    }

    public int getActiveSessionCount() {
        return usernameToSessionMap.values().stream().mapToInt(Set::size).sum();
    }

    public int getActiveUserCount() {
        return (int) usernameToSessionMap.values().stream().filter(s -> !s.isEmpty()).count();
    }
}
