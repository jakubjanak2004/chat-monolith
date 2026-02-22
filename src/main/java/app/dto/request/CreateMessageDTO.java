package app.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

import static app.config.props.ValidationConstraints.MESSAGE_CONTENT_MAX;

@Getter
@AllArgsConstructor
public class CreateMessageDTO {
    @Size(min=1, max=MESSAGE_CONTENT_MAX)
    private String content;
    private UUID replyToId;
}
