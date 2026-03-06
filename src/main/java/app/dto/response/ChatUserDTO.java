package app.dto.response;

import jakarta.validation.constraints.NotNull;

public record ChatUserDTO(
        @NotNull String username,
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull Boolean hasProfilePicture
) {
}
