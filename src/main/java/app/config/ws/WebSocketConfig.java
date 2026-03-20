package app.config.ws;

import app.config.props.WebSocketProperties;
import app.service.ws.UserSessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtDecoder jwtDecoder;
    private final UserSessionRegistry userSessionRegistry;
    private final WebSocketProperties webSocketProperties;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new WSChannelInterceptor(jwtDecoder, userSessionRegistry));
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Raw WebSocket STOMP endpoint
        registry.addEndpoint("/ws-raw")
                .setAllowedOriginPatterns(webSocketProperties.allowedOrigins().toArray(String[]::new));

        // SockJS endpoint used by browser/app clients.
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(webSocketProperties.allowedOrigins().toArray(String[]::new))
                .withSockJS();
    }
}

