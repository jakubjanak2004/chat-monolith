package app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatUserDTO {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean hasProfilePicture;
}
