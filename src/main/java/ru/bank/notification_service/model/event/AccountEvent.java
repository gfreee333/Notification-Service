package ru.bank.notification_service.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.notification_service.model.enums.AccountEventType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountEvent implements IdentifiableEvent {

    private AccountEventType eventType;
    private UUID eventId;
    private UUID userId;
    private String accountNumber;
    private String email;
    private LocalDateTime timestamp;

}
