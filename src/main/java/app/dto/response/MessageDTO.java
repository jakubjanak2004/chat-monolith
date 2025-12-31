package app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageDTO {
    private UUID id;
    private UUID responseToId;
    private UUID chatId;
    private ChatUserDTO sender;
    private Instant created;
    private String content;
}
