package ru.bank.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bank.notification_service.model.event.IdentifiableEvent;
import ru.bank.notification_service.infrastructure.util.JsonUtil;
import ru.bank.notification_service.model.entity.ProcessedEvent;
import ru.bank.notification_service.repository.IdempotentEventRepository;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotentEventService<T extends IdentifiableEvent> {

    private final IdempotentEventRepository eventRepository;
    private final JsonUtil jsonUtil;

    @Transactional
    public boolean processEvent(T event, Consumer<T> processor) {
        try {
            eventRepository.saveAndFlush(ProcessedEvent.builder()
                    .eventId(event.getEventId())
                    .processAt(LocalDateTime.now())
                    .payload(jsonUtil.toJson(event))
                    .build());

        } catch (DataIntegrityViolationException ex) {
            log.debug("Событие: {} уже обработано", event.getEventId());
            return true;
        }
        try {
            processor.accept(event);
            return true;
        } catch (Exception ex) {
            log.warn("Ошибка обработки события: {}, message: {}", event.getEventId(), ex.getMessage());
            throw ex;
        }
    }
}
