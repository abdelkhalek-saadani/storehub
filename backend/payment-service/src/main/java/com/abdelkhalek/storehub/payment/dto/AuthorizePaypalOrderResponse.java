package com.abdelkhalek.storehub.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Data
public class AuthorizePaypalOrderResponse {
    String paymentOrderId;
    String authorizationId;
}
