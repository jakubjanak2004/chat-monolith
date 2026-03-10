package app.config.ws;

import app.service.ws.UserSessionRegistry;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class WSChannelInterceptor implements ChannelInterceptor {
    private final JwtDecoder jwtDecoder;
    private final UserSessionRegistry userSessionRegistry;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            connectAccessor(accessor);
        }

        if (StompCommand.DISCONNECT.equals(command)) {
            disconnectAccessor(accessor);
        }

        return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }

    private void disconnectAccessor(StompHeaderAccessor accessor) {
        Optional.ofNullable(accessor.getUser())
                .ifPresent(user -> userSessionRegistry.removeSessionForUser(accessor.getSessionId(), user.getName()));
    }

    private void connectAccessor(StompHeaderAccessor accessor) {
        String auth = Optional.ofNullable(accessor.getFirstNativeHeader("Authorization"))
                .or(() -> Optional.ofNullable(accessor.getFirstNativeHeader("authorization")))
                .orElseThrow(() -> new IllegalArgumentException("Missing Authorization header"));

        String token = Optional.of(auth)
                .filter(h -> h.startsWith("Bearer "))
                .map(h -> h.substring("Bearer ".length()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid Authorization header (expected: Bearer <accessToken>)"));

        Jwt jwt = jwtDecoder.decode(token);
        String username = jwt.getSubject();

        accessor.setUser(new UsernamePasswordAuthenticationToken(username, null, List.of()));

        Optional.ofNullable(accessor.getSessionId())
                .ifPresent(sessionId -> userSessionRegistry.addSessionForUser(sessionId, username));
    }
}
