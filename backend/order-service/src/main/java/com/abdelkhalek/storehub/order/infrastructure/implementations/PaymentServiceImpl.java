package com.abdelkhalek.storehub.order.infrastructure.implementations;

import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.models.PaymentLink;
import com.abdelkhalek.storehub.order.order.spi.PaymentService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Override
    public Mono<PaymentLink> getPaymentApprovalLink(Order order) {
        return Mono.just(new PaymentLink("link/token"));
//    return Mono.error(new RuntimeException("Not implemented"));
    }
}
