package app.dto.response;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class InvitationDTO {
    @NotNull
    private UUID id;
    @Valid
    @NotNull
    private ChatUserDTO chatUser;
}
