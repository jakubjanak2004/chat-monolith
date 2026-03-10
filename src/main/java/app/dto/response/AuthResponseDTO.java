package app.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AuthResponseDTO(
        @NotNull
        UUID userId,
        @NotNull
        String accessToken,
        @NotBlank
        String refreshToken,
        @NotNull
        String username,
        @NotNull
        String email,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @NotNull
        Boolean hasProfilePicture
) {
}
