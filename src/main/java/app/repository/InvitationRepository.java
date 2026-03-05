package app.repository;

import app.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    List<Invitation> findAllByChatUser_Username(String username);
    List<Invitation> findAllByChat_Id(UUID chatId);
    Optional<Invitation> findFirstByChat_IdAndChatUser_Username(UUID chatId, String username);
}
