package app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChatDTO {
    private String id;
    private String name;
    private List<ChatUserDTO> chatUsers;
    private MessageDTO lastMessage;
}
