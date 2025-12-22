package app.repository;

import app.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    @Query("""
                select c
                from Chat c
                join c.chatMemberships cm
                where cm.chatUser.username = :username
                  and (:query is null or :query = '' 
                       or c.nameNormalized like concat('%', :query, '%'))
            """)
    Page<Chat> findByNameNormAndUsername(
            @Param("query") String query,
            @Param("username") String username,
            Pageable pageable
    );

}
