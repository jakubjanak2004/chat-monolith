package app.config.props;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.security")
@Validated
public record SecurityProperties(
        @Min(8) int saltLength,
        @Min(16) int hashLength,
        @Min(1) int parallelism,
        @Min(1024) int memoryKb,
        @Min(1) int iterations
) {}
