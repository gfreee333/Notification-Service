package ru.bank.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bank.notification_service.model.entity.ProcessedEvent;

@Repository
public interface IdempotentEventRepository extends JpaRepository<ProcessedEvent, Long> {
}
