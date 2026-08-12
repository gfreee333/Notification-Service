package ru.bank.notification_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.email.from}")
    private String fromEmail;

    public void sendSimpleMessage(String to, String subject, String text){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to); // На какую почту мы делаем отправку
            message.setSubject(subject); // Тема сообщения
            message.setText(text); // Конкретное сообщение для пользователя
            mailSender.send(message);
        } catch (Exception ex){ // Можно сделать конкретное сообщение об ошибке, если нужно
            throw new RuntimeException("Email sending exception");
        }
    }

}
