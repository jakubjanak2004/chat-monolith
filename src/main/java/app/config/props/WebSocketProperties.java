package app.config.props;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties(prefix = "app.websocket")
@Validated
public record WebSocketProperties(
        @NotEmpty List<String> allowedOrigins
) {}
