package app.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageDTO {
    @NotNull
    private UUID id;
    private UUID responseToId;
    private ChatUserDTO responseToSender;
    private String responseToContent;
    @NotNull
    private UUID chatId;
    @NotNull
    private ChatUserDTO sender;
    @NotNull
    private Instant created;
    @NotNull
    private String content;
}
