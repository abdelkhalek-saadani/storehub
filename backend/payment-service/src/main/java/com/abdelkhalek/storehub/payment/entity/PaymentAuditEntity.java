package com.abdelkhalek.storehub.payment.entity;

import com.abdelkhalek.storehub.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table( name ="payment_audit")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus oldStatus;
    @Enumerated(EnumType.STRING)
    private PaymentStatus newStatus;
    private String reason;

    private String createdBy;
    private LocalDateTime timestamp;

}
