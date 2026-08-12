package ru.bank.notification_service.infrastructure.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.bank.notification_service.infrastructure.util.SimpleDecrypt;
import ru.bank.notification_service.model.event.AuthEvent;
import ru.bank.notification_service.service.EmailService;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthEventConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = "auth-password-topic",
            containerFactory = "authEventKafkaListenerContainerFactory"
    )
    public void handlerPasswordEvent(AuthEvent event, Acknowledgment ask){
        emailService.sendSimpleMessage(
                event.getEmail(),
                "Пароль для входа в систему",
                "Здравствуйте, " + event.getFirstName() + " " + event.getLastName()
                 + "используете данный Password: " + SimpleDecrypt.decrypt(event.getEncryptedPassword())
                 + " для входа в систему");
        ask.acknowledge();
    }

    @KafkaListener(
            topics = "auth-information-topic",
            containerFactory = "authEventKafkaListenerContainerFactory"
    )
    public void handlerInformationEvent(){

    }

    @KafkaListener(
            topics = "auth-blocked-topic",
            containerFactory = "authEventKafkaListenerContainerFactory"
    )
    public void handlerBlockedEvent(){

    }

    @KafkaListener(
            topics = "auth-unblocked-topic",
            containerFactory = "authEventKafkaListenerContainerFactory"
    )
    public void handlerUnblockedEvent(){

    }

    @KafkaListener(
            topics = "auth-delete-user-topic",
            containerFactory = "authEventKafkaListenerContainerFactory"
    )
    public void handlerDeleteUserEvent(){

    }

}
