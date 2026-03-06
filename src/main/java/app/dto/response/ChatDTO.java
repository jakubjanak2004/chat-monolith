package app.dto.response;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ChatDTO(
        @NotNull
        UUID id,
        @NotNull
        String name,
        List<ChatUserDTO> chatUsers,
        MessageDTO lastMessage
) {
}
