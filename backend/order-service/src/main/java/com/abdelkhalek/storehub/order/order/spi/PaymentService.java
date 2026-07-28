package com.abdelkhalek.storehub.order.order.spi;

import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.dto.PaymentResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface PaymentService {

    Mono<PaymentResponse> getPaymentApprovalLink(Order order);

}
