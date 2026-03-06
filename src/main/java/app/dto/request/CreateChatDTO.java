package app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateChatDTO(
        @NotBlank
        String name,
        @NotEmpty
        List<String> membersList
) {
}
