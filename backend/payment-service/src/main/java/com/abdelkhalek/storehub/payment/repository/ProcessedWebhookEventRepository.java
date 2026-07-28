package com.abdelkhalek.storehub.payment.repository;


import com.abdelkhalek.storehub.payment.entity.ProcessedWebhookEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedWebhookEventRepository extends JpaRepository<ProcessedWebhookEventEntity, String> {
}
