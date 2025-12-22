package app.repository;

import app.entity.ChatUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ChatUserRepository extends JpaRepository<ChatUser, UUID> {
    Optional<ChatUser> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("""
              select u
              from ChatUser u
              where (:query is null or :query = '' or u.nameNormalized like concat('%', :query, '%'))
                and (:excludeUsername is null or u.username <> :excludeUsername)
            """)
    Page<ChatUser> findByNameNormNotUsername(
            @Param("query") String query,
            @Param("excludeUsername") String excludeUsername,
            Pageable pageable
    );

}
