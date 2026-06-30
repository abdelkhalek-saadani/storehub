package com.abdelkhalek.storehub.order.domain.spi;

import com.abdelkhalek.storehub.order.domain.models.Order;
import com.abdelkhalek.storehub.order.domain.models.PaymentLink;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface PaymentService {

    Mono<PaymentLink> getPaymentApprovalLink(Order order);

}
