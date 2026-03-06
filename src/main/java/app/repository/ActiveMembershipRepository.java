package app.repository;

import app.entity.ActiveMembership;
import app.enumeration.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActiveMembershipRepository extends JpaRepository<ActiveMembership, UUID> {
    List<ActiveMembership> findAllByChat_Id(UUID chatId);

    long countByChat_IdAndMembershipType(UUID chatId, MembershipType type);

    Optional<ActiveMembership> findFirstByChat_IdAndChatUser_Username(UUID chatId, String username);
}
