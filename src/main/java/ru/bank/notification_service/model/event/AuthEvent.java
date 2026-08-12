package ru.bank.notification_service.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.notification_service.model.enums.AuthEventType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthEvent {
    private AuthEventType authEventType;
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String encryptedPassword;
    private LocalDateTime timestamp;
}
