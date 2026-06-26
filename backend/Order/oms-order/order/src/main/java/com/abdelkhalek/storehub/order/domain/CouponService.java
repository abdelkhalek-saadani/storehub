package com.abdelkhalek.storehub.order.domain;

import com.abdelkhalek.storehub.order.domain.models.Coupon;
import com.abdelkhalek.storehub.order.domain.models.Order;
import reactor.core.publisher.Mono;

public interface CouponService {

    Mono<Boolean> validateCoupon(Coupon coupon);

    Mono<Order> applyCoupon(Order order, Coupon coupon);

    Mono<Void> markCouponUnused(Coupon coupon);

}
