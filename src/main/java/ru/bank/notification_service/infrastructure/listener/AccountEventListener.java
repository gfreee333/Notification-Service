package ru.bank.notification_service.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.bank.notification_service.model.event.AccountEvent;
import ru.bank.notification_service.service.EmailService;
import ru.bank.notification_service.service.EmailTemplateService;
import ru.bank.notification_service.service.IdempotentEventService;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountEventListener {

    private final EmailService emailService;
    private final IdempotentEventService<AccountEvent> idempotentEventService;
    private final EmailTemplateService emailTemplateService;

    @KafkaListener(
            topics = "account-registration-topic",
            containerFactory = "accountEventKafkaListenerContainerFactory"
    )
    public void accountRegistrationProcess(AccountEvent event, Acknowledgment ack){
        process(event, ack, e -> {
            String body = emailTemplateService.renderAccountTemplate(
                    "/account-events/account-registration",
                    event.getAccountNumber(),
                    event.getTimestamp());
            emailService.sendHtmlMessage(e.getEmail(), "Информация об открытие нового счета", body);
        });
    }

    @KafkaListener(
            topics = "account-blocked-topic",
            containerFactory = "accountEventKafkaListenerContainerFactory"
    )
    public void accountBlockedProcess(AccountEvent event, Acknowledgment ack){
        process(event, ack, e -> {
            String body = emailTemplateService.renderAccountTemplate(
                    "/account-events/account-blocked",
                    event.getAccountNumber(),
                    event.getTimestamp());
            emailService.sendHtmlMessage(e.getEmail(), "Информация о блокировки счета", body);
        });
    }

    @KafkaListener(
            topics = "account-unblocked-topic",
            containerFactory = "accountEventKafkaListenerContainerFactory"
    )
    public void accountUnlockedProcess(AccountEvent event, Acknowledgment ack){
        process(event, ack, e -> {
            String body = emailTemplateService.renderAccountTemplate(
                    "/account-events/account-unblocked",
                    event.getAccountNumber(),
                    event.getTimestamp());
            emailService.sendHtmlMessage(e.getEmail(), "Информация о разблокировки счета", body);
        });
    }

    @KafkaListener(
            topics = "account-close-topic",
            containerFactory = "accountEventKafkaListenerContainerFactory"
    )
    public void accountClosedProcess(AccountEvent event, Acknowledgment ack){
        process(event, ack, e -> {
            String body = emailTemplateService.renderAccountTemplate(
                    "/account-events/account-closed",
                    event.getAccountNumber(),
                    event.getTimestamp());
            emailService.sendHtmlMessage(e.getEmail(), "Информация о закрытие счета", body);
        });
    }

    private void process(AccountEvent event, Acknowledgment ack, Consumer<AccountEvent> handler) {
        boolean success = idempotentEventService.processEvent(event, handler);
        if (success) {
            ack.acknowledge();
        }
    }

}
