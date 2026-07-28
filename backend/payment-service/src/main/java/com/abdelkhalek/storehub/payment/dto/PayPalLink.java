package com.abdelkhalek.storehub.payment.dto;

public record PayPalLink(
        String href,
        String rel,
        String method
) {}
