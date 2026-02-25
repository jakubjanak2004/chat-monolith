package app.service.ws;

import app.config.ws.WebSocketConfig;
import app.dto.response.MessageDTO;
import app.entity.ChatMembership;
import app.entity.Message;
import app.event.MessageCreatedEvent;
import app.mapper.MessageMapper;
import app.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class ChatWSPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatWSPublisher.class);
    private final SimpMessagingTemplate template;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final UserSessionRegistry userSessionRegistry;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent e) {
        Message message = messageRepository.findById(e.messageId()).orElseThrow();
        MessageDTO messageDTO = messageMapper.toDTO(message);

        LOGGER.info("WS publish messageId={}, chatId={}, memberships={}",
                e.messageId(), e.chatId(), message.getChat().getChatMemberships().size());

        message.getChat().getChatMemberships().stream()
                .map(ChatMembership::getChatUser)
                .peek(chatUser -> LOGGER.info("WS sendToUser username={}", chatUser.getUsername()))
                .flatMap(chatUser -> userSessionRegistry.getSessionSet(chatUser.getUsername()).stream())
                .forEach(sessionId -> template.convertAndSend("/queue/messages-user" + sessionId, messageDTO));
    }
}
