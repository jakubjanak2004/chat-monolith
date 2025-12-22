package app.entity;

import app.enumeration.MembershipType;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ActiveMembership extends ChatMembership {
    private MembershipType membershipType;
}
