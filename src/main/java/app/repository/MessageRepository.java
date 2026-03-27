package app.repository;

import app.entity.Chat;
import app.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    Page<Message> findAllByChat(Chat chat, Pageable pageable);

    Integer countByChat_Id(UUID chatId);

    @Query("""
            select m
            from Message m
            join m.chat c
            where c.id = :chatId
            and (
            :query is null or :query = ''
            or m.content like concat('%', :query, '%')
            )
            """)
    Page<Message> findMessages(@Param("chatId") UUID chatId, @Param("query") String query, Pageable pageable);
}
