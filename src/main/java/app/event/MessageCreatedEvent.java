package app.event;

import java.util.UUID;

public record MessageCreatedEvent(UUID messageId, UUID chatId) {
}
