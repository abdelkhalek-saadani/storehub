package com.proxiad.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreatePaypalOrderResponse {

    private String paymentOrderId;
    private String approvalUrl;

}
