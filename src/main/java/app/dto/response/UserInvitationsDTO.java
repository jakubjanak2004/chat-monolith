package app.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserInvitationsDTO {
    @NotNull
    private UUID id;
    @NotNull
    private String chatName;
    @NotNull
    private List<ChatUserDTO> chatUsers;
}
