package com.abdelkhalek.storehub.order.order.spi;

import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.dto.PaymentResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public interface PaymentService {

    Mono<PaymentResponse> getPaymentApprovalLink(Order order);

    Mono<PaymentResponse> voidAuthorizedPayment(UUID orderId);
}
