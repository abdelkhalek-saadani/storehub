package com.proxiad.payment.dto;

import com.proxiad.payment.entity.MoneyEntity;
import com.proxiad.payment.entity.PaymentEntity;
import com.proxiad.payment.enums.PaymentStatus;


import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentDTO {

    private UUID id;

    private UUID orderId;

    private MoneyEntity amount;

    private String approvalUrl;

    private String paymentOrderId;

    private String authorizationId;

    private PaymentStatus status;

    private String captureId;

    private String refundId;

    private UUID customerId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    PaymentDTO(PaymentEntity paymentEntity) {
        this.id = paymentEntity.getId();
        this.orderId = paymentEntity.getOrderId();
        this.amount = paymentEntity.getAmount();
        this.approvalUrl = paymentEntity.getApprovalUrl();
        this.paymentOrderId = paymentEntity.getPaymentOrderId();
        this.authorizationId = paymentEntity.getAuthorizationId();
        this.status = paymentEntity.getStatus();
        this.captureId = paymentEntity.getCaptureId();
        this.refundId = paymentEntity.getRefundId();
        this.customerId = paymentEntity.getCustomerId();
        this.createdAt = paymentEntity.getCreatedAt();
        this.updatedAt = paymentEntity.getUpdatedAt();
    }
}



