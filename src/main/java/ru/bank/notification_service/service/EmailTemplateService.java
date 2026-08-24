package ru.bank.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateService {

    private final SpringTemplateEngine templateEngine;

    public String renderAccountTemplate(String template,
                                        String accountNumber){
        return render(template, Map.of("accountNumber", accountNumber));
    }

    public String renderPasswordTemplate(String firstName, String lastName, String password) {
        return render("/auth-events/password-event",
                Map.of( "firstName", firstName,
                        "lastName", lastName,
                        "password", password));
    }

    public String renderChangePasswordTemplate(String firstName, String lastName, LocalDateTime timestamp) {
        return render("/auth-events/password-change-event",
                Map.of( "firstName", firstName,
                        "lastName", lastName,
                        "timestamp", timestamp));
    }

    public String renderAuthBlockedTemplate(String firstName, String lastName, LocalDateTime timestamp) {
        return render("/auth-events/blocked-event",
                Map.of( "firstName", firstName,
                        "lastName", lastName,
                        "timestamp", timestamp));
    }

    public String rendersAuthUnblockedTemplate(String firstName, String lastName, LocalDateTime timestamp) {
        return render("/auth-events/unblocked-event",
                Map.of( "firstName", firstName,
                        "lastName", lastName,
                        "timestamp", timestamp));
    }

    public String rendersUserDeleteTemplate(String firstName, String lastName, LocalDateTime timestamp) {
        return render("/auth-events/delete-user-event",
                Map.of( "firstName", firstName,
                        "lastName", lastName,
                        "timestamp", timestamp));
    }

    private String render(String template, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(template, context);
    }

}
