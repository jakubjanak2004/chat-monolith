package app.service.ws;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class UserSessionRegistry {
    private final ConcurrentMap<String, Set<String>> usernameToSessionSet = new ConcurrentHashMap<>();

    public Set<String> getSessionSet(String username) {
        return usernameToSessionSet.computeIfAbsent(username, key -> new HashSet<>());
    }

    public void removeSessionForUser(String sessionId, String username) {
        getSessionSet(username).remove(sessionId);
    }

    public void addSessionForUser(String sessionId, String username) {
        getSessionSet(username).add(sessionId);
    }

    public int getActiveSessionCount() {
        return usernameToSessionSet.values().stream().mapToInt(Set::size).sum();
    }

    public int getActiveUserCount() {
        return (int) usernameToSessionSet.values().stream().filter(s -> !s.isEmpty()).count();
    }
}
