package com.abdelkhalek.storehub.order.infrastructure.implementations;

import com.abdelkhalek.storehub.order.domain.CouponService;
import com.abdelkhalek.storehub.order.domain.models.Coupon;
import com.abdelkhalek.storehub.order.domain.models.Money;
import com.abdelkhalek.storehub.order.domain.models.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Slf4j
@Service
public class CouponServiceImpl implements CouponService {
    @Override
    public Mono<Boolean> validateCoupon(Coupon coupon) {
        log.info("Validating coupon {} ", coupon);
        //if (coupon==null) return Mono.just(true);
        return Mono.just(true);
    }

    @Override
    public Mono<Order> applyCoupon(Order order, Coupon coupon) {
        log.info("Applying coupon {} for order {} and eventually marking it used", coupon, order);
        return Mono.fromSupplier(() ->{
            log.info("Applying a discount of 10%");
            BigDecimal totalValueAfterDiscount = order.getTotal().getValue()
                    .multiply(BigDecimal.valueOf(90))
                    .divide(BigDecimal.valueOf(100),new MathContext(10, RoundingMode.HALF_UP));
            order.setTotal(new Money(totalValueAfterDiscount));
            return order;
        });
    }

    @Override
    public Mono<Void> markCouponUnused(Coupon coupon) {
        log.info("Marking coupon {}  as unused", coupon);
        return Mono.empty();
    }

}
