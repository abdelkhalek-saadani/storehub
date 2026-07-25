package com.proxiad.payment.dto;


import java.util.UUID;


public record PaymentResponse(
        UUID paymentId,
        String status,
        String approvalUrl,
        String message
) {
}