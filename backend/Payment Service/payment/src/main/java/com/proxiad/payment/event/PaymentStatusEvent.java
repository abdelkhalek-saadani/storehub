package com.proxiad.payment.event;

import com.proxiad.payment.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentStatusEvent {

    private UUID paymentId;
    private UUID orderId;
    private PaymentStatus status; // APPROVED, AUTHORIZED, CAPTURED, REFUNDED
    private LocalDate timestamp;

}
