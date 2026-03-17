package app.event;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MessageCreatedEvent(
        @NotNull UUID messageId,
        @NotNull UUID chatId) {
}
