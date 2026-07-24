package com.proxiad.payment.dto;

public record PayPalLink(
        String href,
        String rel,
        String method
) {}
