package com.proxiad.payment.dto;

import java.util.List;

public record PayPalOrderResponse(
        String id,
        String status,
        List<PayPalLink> links
) {}

