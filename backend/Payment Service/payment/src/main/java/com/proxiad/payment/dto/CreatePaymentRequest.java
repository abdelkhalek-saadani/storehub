package com.proxiad.payment.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class CreatePaymentRequest {
    private UUID orderId;
    private UUID customerId;
    private String amount;
    private String currency = "EUR";

    CreatePaymentRequest(String amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    // Getters and setters
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}