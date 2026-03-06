package app.dto.request;

import app.enumeration.MembershipType;
import jakarta.validation.constraints.NotNull;

public record ActiveMembershipUpdateDTO(@NotNull MembershipType membershipType) {
}
