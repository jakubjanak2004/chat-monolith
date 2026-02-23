package app.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChatDTO {
    @NotNull
    private String id;
    @NotNull
    private String name;
    private List<ChatUserDTO> chatUsers;
    private MessageDTO lastMessage;
}
