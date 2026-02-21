package app.repository;

import app.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
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

    @Query("""
        select c
        from Chat c
        join c.chatMemberships cmAll
        left join c.chatMemberships cmFilter
               on cmFilter.chatUser.username in :usernames
        group by c
        having count(distinct cmFilter.chatUser.username) = :#{#usernames.size()}
           and count(distinct cmAll.chatUser.username)   = :#{#usernames.size()}
    """)
    List<Chat> findChatsWithExactlyParticipants(@Param("usernames") Collection<String> usernames);
}
