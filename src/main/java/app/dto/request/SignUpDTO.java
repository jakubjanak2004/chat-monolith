package app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static app.config.ValidationConstraints.PASSWORD_MAX_LENGTH;
import static app.config.ValidationConstraints.PASSWORD_MIN_LENGTH;
import static app.config.ValidationConstraints.USERNAME_MAX_LENGTH;
import static app.config.ValidationConstraints.USERNAME_MIN_LENGTH;

@Getter
@Setter
@AllArgsConstructor
public class SignUpDTO {
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH)
    private String username;
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;
    @Email
    private String email;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
