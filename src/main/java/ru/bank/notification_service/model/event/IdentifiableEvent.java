package ru.bank.notification_service.model.event;

import java.util.UUID;

public interface IdentifiableEvent {
    UUID getEventId();
}
