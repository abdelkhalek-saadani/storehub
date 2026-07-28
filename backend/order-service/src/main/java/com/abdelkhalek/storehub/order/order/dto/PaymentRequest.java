package com.abdelkhalek.storehub.order.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
        UUID orderId,
        UUID customerId,
        BigDecimal amount
) {
}
