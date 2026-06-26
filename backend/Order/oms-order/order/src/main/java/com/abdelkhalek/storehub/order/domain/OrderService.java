package com.abdelkhalek.storehub.order.domain;

import com.abdelkhalek.storehub.order.domain.models.*;
import com.abdelkhalek.storehub.order.domain.models.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderService {

    Mono<Order> placeOrderWithCashPayment(Cart cart, Delivery delivery, Slot slot, Coupon coupon, Store store);

    Mono<PaymentLink> placeOrderWithOnlinePayment(Cart cart, Delivery delivery, Slot slot, Coupon coupon, Store store);

    public Mono<PaymentLink> getPaymentApprovalLinkById(UUID orderId);


}
