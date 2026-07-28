package com.proxiad.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PayPalAuthorizeResponse(
        String id,
        String status,
        @JsonProperty("purchase_units") List<PayPalPurchaseUnitAuthorized> purchaseUnits
) {
    public record PayPalPurchaseUnitAuthorized(
            PayPalPayments payments
    ) {
        public record PayPalPayments(
                List<PayPalAuthorization> authorizations
        ) {
            public record PayPalAuthorization(
                    String id,
                    String status
            ) {}
        }

    }

}
