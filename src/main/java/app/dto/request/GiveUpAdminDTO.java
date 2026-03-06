package app.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GiveUpAdminDTO(
        @NotBlank
        String successorUsername
) {
}
