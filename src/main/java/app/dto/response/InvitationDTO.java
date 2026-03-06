package app.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InvitationDTO(
        @NotNull UUID id,
        @Valid @NotNull ChatUserDTO chatUser
) {
}