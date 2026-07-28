package com.abdelkhalek.storehub.payment.dto;

import com.abdelkhalek.storehub.payment.entity.PaymentEntity;
import com.abdelkhalek.storehub.payment.enums.PaymentStatus;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentDTO {

    private UUID id;

    private UUID orderId;

    private BigDecimal amount;

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



