package com.proxiad.payment.repository;


import com.proxiad.payment.entity.ProcessedWebhookEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedWebhookEventRepository extends JpaRepository<ProcessedWebhookEventEntity, String> {
}
