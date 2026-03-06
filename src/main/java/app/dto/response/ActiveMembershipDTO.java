package app.dto.response;

import app.enumeration.MembershipType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ActiveMembershipDTO(
        @NotNull
        UUID id,
        @Valid
        @NotNull
        ChatUserDTO chatUser,
        @NotNull
        MembershipType membershipType
) {
}
