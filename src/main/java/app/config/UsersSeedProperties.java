package app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.seed.users")
public record UsersSeedProperties(
        boolean enabled,
        int count,
        String password
) {}