package app.repository;

import app.entity.ChatMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatMembershipRepository extends JpaRepository<ChatMembership, UUID> {
    Optional<ChatMembership> findByChat_IdAndChatUser_Username(UUID chatId, String username);
}
