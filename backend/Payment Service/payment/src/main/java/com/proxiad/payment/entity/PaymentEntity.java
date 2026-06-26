package com.proxiad.payment.entity;

import com.proxiad.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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

    private UUID orderId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "amount_value")),
            @AttributeOverride(name = "currency", column = @Column(name = "amount_currency"))
    })
    private MoneyEntity amount;

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
