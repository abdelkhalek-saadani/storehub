package com.abdelkhalek.storehub.payment.repository;


import com.abdelkhalek.storehub.payment.entity.ProcessedWebhookEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessedWebhookEventRepository extends JpaRepository<ProcessedWebhookEventEntity, String> {
    Optional<ProcessedWebhookEventEntity> findByEventId(String id);
}
