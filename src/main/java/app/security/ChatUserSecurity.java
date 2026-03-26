package app.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("chatUserSecurity")
@RequiredArgsConstructor
public class ChatUserSecurity {
    public boolean hasUsername(String username, Authentication authentication) {
        return Objects.equals(authentication.getName(), username);
    }
}
