package com.abdelkhalek.storehub.payment.entity;

import com.abdelkhalek.storehub.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table( name = "payment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private UUID orderId;


    private BigDecimal amount;

    private String approvalUrl;

    private String paymentOrderId;

    private String authorizationId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String captureId;

    private String refundId;

    private UUID customerId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
