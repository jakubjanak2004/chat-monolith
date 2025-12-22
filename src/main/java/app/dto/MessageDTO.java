package app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageDTO {
    private UUID id;
    private UUID responseToId;
    private String content;
}
