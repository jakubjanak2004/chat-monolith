package app.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        int saltLength,
        int hashLength,
        int parallelism,
        int memoryKb,
        int iterations
) {}
