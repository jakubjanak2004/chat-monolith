package app.observability;

import app.service.ws.UserSessionRegistry;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class WebSocketMetrics {
    private final Timer messageDeliveryLag;

    public WebSocketMetrics(MeterRegistry registry, UserSessionRegistry userSessionRegistry) {
        Gauge.builder("chat_ws_active_sessions", userSessionRegistry, UserSessionRegistry::getActiveSessionCount)
                .description("Number of active WebSocket sessions (all users).")
                .register(registry);

        Gauge.builder("chat_ws_active_users", userSessionRegistry, UserSessionRegistry::getActiveUserCount)
                .description("Number of users with at least one active WebSocket session.")
                .register(registry);

        this.messageDeliveryLag = Timer.builder("chat_ws_message_delivery_lag")
                .description("Time from message creation to WS publish.")
                .publishPercentiles(0.5, 0.95, 0.99)
                .publishPercentileHistogram()
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofSeconds(10))
                .register(registry);
    }

    public Timer messageDeliveryLag() {
        return messageDeliveryLag;
    }
}

