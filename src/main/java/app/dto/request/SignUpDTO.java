package app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static app.config.props.ValidationConstraints.PASSWORD_MAX_LENGTH;
import static app.config.props.ValidationConstraints.PASSWORD_MIN_LENGTH;
import static app.config.props.ValidationConstraints.USERNAME_MAX_LENGTH;
import static app.config.props.ValidationConstraints.USERNAME_MIN_LENGTH;

public record SignUpDTO(
        @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH)
        String username,
        @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
        String password,
        @Email
        String email,
        @NotBlank
        String firstName,
        @NotBlank
        String lastName
) {
}
