package app.listener;

import app.event.UserCreatedEvent;
import app.service.ChatUserMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatUserEmailListener {
    private final ChatUserMailService chatUserMailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserCreatedEvent event) {
        chatUserMailService.sendWelcomeEmail(event);
    }
}
