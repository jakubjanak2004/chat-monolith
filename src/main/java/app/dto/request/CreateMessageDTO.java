package app.dto.request;

import jakarta.validation.constraints.Size;

import java.util.UUID;

import static app.config.props.ValidationConstraints.MESSAGE_CONTENT_MAX;

public record CreateMessageDTO(
        @Size(min = 1, max = MESSAGE_CONTENT_MAX)
        String content,
        UUID replyToId
) {
}
