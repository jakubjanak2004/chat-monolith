package app.config.props;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.seed.users")
public record UsersSeedProperties(
        @DefaultValue("false") boolean enabled,
        @DefaultValue("10") @Min(1) int count,
        @DefaultValue("password") @NotBlank String password
) {
}