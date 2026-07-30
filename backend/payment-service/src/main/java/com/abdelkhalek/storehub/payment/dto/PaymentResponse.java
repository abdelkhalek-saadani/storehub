package com.abdelkhalek.storehub.payment.dto;


import java.util.UUID;


public record PaymentResponse(
        UUID paymentId,
        String paymentOrderId,    // token
        String status,
        String approvalUrl,
        String message
) {
}