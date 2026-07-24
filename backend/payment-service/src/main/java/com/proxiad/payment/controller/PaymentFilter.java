package com.proxiad.payment.controller;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentFilter {
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}