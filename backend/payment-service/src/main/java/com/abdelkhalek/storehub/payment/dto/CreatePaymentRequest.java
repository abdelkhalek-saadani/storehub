package com.proxiad.payment.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;


public record CreatePaymentRequest(
         UUID orderId,
         UUID customerId,
         BigDecimal amount
) {
}