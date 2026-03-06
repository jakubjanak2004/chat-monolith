package app.config.props;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.seed.admin")
@Validated
public record AdminSeedProperties(
        @DefaultValue("false") boolean enabled,
        @DefaultValue("admin") String username,
        @DefaultValue("admin") String password,
        @DefaultValue("admin@example.com") String email,
        @DefaultValue("Admin") String firstName,
        @DefaultValue("User") String lastName,
        @DefaultValue("0") @Min(0) int numOfMessages,
        @DefaultValue("3") @Min(1) int messageWordCountFrom,
        @DefaultValue("8") @Min(1) int messageWordCountTo
) {
}
