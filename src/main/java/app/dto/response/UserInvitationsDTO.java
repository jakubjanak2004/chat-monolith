package app.dto.response;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record UserInvitationsDTO(
        @NotNull UUID id,
        @NotNull String chatName,
        @NotNull List<ChatUserDTO> chatUsers
) {
}