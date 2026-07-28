package com.abdelkhalek.storehub.order.order.service;

import com.abdelkhalek.storehub.order.order.dto.OrderCreatedResponse;
import com.abdelkhalek.storehub.order.order.dto.PaymentResponse;
import com.abdelkhalek.storehub.order.order.exceptions.PaymentProcessingException;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.spi.OrderRepository;
import com.abdelkhalek.storehub.order.order.spi.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderPaymentService {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    public Mono<OrderCreatedResponse> attachPaymentAndSave(Order order) {
        return getPaymentApprovalLink(order)
                .flatMap(paymentResponse -> {
                    order.setPaymentId(paymentResponse.paymentId());
                    order.setPaymentApprovalLink(paymentResponse.approvalUrl());
                    return orderRepository.save(order);
                })
                .map(OrderCreatedResponse::from)
                .onErrorMap(e -> new PaymentProcessingException("Failed to get payment approval link", e));
    }


    private Mono<PaymentResponse> getPaymentApprovalLink(Order order) {
        return paymentService.getPaymentApprovalLink(order);
    }

}
