package app.listener;

import app.event.MessageCreatedEvent;
import app.service.ws.ChatWsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MessageListener {
    private final ChatWsService chatWsService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent messageCreatedEvent) {
        chatWsService.messageCreated(messageCreatedEvent);
    }
}
