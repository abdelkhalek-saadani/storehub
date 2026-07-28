package com.abdelkhalek.storehub.payment.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "processed_webhook_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedWebhookEventEntity {

    @Id
    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;
}
