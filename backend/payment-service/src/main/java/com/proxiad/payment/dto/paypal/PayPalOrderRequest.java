package com.proxiad.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record PayPalOrderRequest(
        String intent,
        @JsonProperty("purchase_units") List<PayPalPurchaseUnit> purchaseUnits,
        @JsonProperty("application_context") PayPalApplicationContext applicationContext
) {

    public record PayPalPurchaseUnit(
            PayPalAmount amount,
            String description
    ) {

        public record PayPalAmount(
                @JsonProperty("currency_code") String currencyCode,
                BigDecimal value
        ) {}
    }

    public record PayPalApplicationContext(
            @JsonProperty("return_url") String returnUrl,
            @JsonProperty("cancel_url") String cancelUrl
    ) {}



}