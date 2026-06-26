package com.proxiad.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthorizePaypalOrderResponse {
    String paymentOrderId;
    String authorizationId;
}
