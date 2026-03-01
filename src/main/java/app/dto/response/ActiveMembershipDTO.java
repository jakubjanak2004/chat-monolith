package app.dto.response;

import app.enumeration.MembershipType;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ActiveMembershipDTO {
    @NonNull
    private UUID id;
    @Valid
    private ChatUserDTO chatUser;
    @NonNull
    private MembershipType membershipType;
}
