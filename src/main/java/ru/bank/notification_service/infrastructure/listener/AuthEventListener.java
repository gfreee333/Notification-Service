package ru.bank.notification_service.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.bank.notification_service.service.IdempotentEventService;
import ru.bank.notification_service.infrastructure.util.SimpleDecrypt;
import ru.bank.notification_service.model.event.AuthEvent;
import ru.bank.notification_service.service.EmailService;
import ru.bank.notification_service.service.EmailTemplateService;

import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthEventListener {

    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;
    private final IdempotentEventService<AuthEvent> idempotentEventService;

    @KafkaListener(
            topics = "auth-password-topic",
            containerFactory = "authEventKafkaListenerContainerFactory"
    )
    public void processPasswordEvent(AuthEvent event, Acknowledgment ask) {
        process(event, ask, e -> {
            String body = emailTemplateService.renderPasswordTemplate(
                    e.getFirstName(), e.getLastName(), SimpleDecrypt.decrypt(e.getEncryptedPassword())
            );
            emailService.sendHtmlMessage(e.getEmail(), "Новый пароль для входа в систему", body);
        });
    }

    @KafkaListener(
            topics = "auth-information-topic",
            containerFactory = "authEventKafkaListenerContainerFactory"
    )
    public void processInformationEvent(AuthEvent event, Acknowledgment ack) {
        process(event, ack, e -> {
            String body = emailTemplateService.renderChangePasswordTemplate(
                    e.getFirstName(), e.getLastName(), e.getTimestamp()
            );
            emailService.sendHtmlMessage(e.getEmail(), "Информация о смене пароля пользователем", body);
        });
    }

    @KafkaListener(
            topics = "auth-blocked-topic",
            containerFactory = "authEventKafkaListenerContainerFactory"
    )
    public void handlerBlockedEvent(AuthEvent event, Acknowledgment ack) {
        process(event, ack, e -> {
            String body = emailTemplateService.renderBlockedTemplate(
                    e.getFirstName(), e.getLastName(), e.getTimestamp()
            );
            emailService.sendHtmlMessage(e.getEmail(), "Информация о блокировке аккаунта", body);
        });
    }

    @KafkaListener(
            topics = "auth-unblocked-topic",
            containerFactory = "authEventKafkaListenerContainerFactory"
    )
    public void handlerUnblockedEvent(AuthEvent event, Acknowledgment ack) {
        process(event, ack, e -> {
            String body = emailTemplateService.rendersUnblockedTemplate(
                    e.getFirstName(), e.getLastName(), e.getTimestamp()
            );
            emailService.sendHtmlMessage(e.getEmail(), "Информация о разблокировке аккаунта", body);
        });
    }

    @KafkaListener(
            topics = "auth-delete-user-topic",
            containerFactory = "authEventKafkaListenerContainerFactory"
    )
    public void handlerDeleteUserEvent(AuthEvent event, Acknowledgment ack) {
        process(event, ack, e -> {
            String body = emailTemplateService.rendersUserDeleteTemplate(
                    e.getFirstName(), e.getLastName(), e.getTimestamp()
            );
            emailService.sendHtmlMessage(e.getEmail(), "Информация об удаление аккаунта из системы", body);
        });
    }

    private void process(AuthEvent event, Acknowledgment ack, Consumer<AuthEvent> handler) {
        boolean success = idempotentEventService.processEvent(event, handler);
        if (success) {
            ack.acknowledge();
        }
    }

}
