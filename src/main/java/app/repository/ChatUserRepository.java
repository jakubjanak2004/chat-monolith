package app.repository;

import app.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatUserRepository extends JpaRepository<ChatUser, UUID> {
    Optional<ChatUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
