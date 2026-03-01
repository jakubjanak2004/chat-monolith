package app.dto.request;

import app.enumeration.MembershipType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class ActiveMembershipUpdateDTO {
    @NonNull
    private MembershipType membershipType;
}
