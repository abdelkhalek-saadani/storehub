package com.abdelkhalek.storehub.order.application.models;

import com.abdelkhalek.storehub.order.application.models.cart.CartDTO;
import com.abdelkhalek.storehub.order.application.models.delivery.DeliveryDTO;

public class CreateOrderDTO {
    /*
     * Delivery {
     *   deliveryMode (Enum: Pickup or Home Delivery),
     *   address (@Client for the second mode, @Store for the first mode)
     * }
     *
     * Cart {
     *   items,
     *   total (includes delivery fee if applicable)
     *   // total is used to verify that the amount displayed to the user is consistent
     *   // not sure whether to include it here or in an object with the payment method,
     *   // as the user also uses it during the payment process
     * }
     *
     * Coupon {
     *   couponCode // needs to be validated
     * }
     *
     * TimeSlot {
     *   date,
     *   startTime,
     *   endTime
     * }
     *
     * Payment or PaymentMethod {
     *   paymentMode,
     *   total
     * }
     */
    DeliveryDTO delivery;
    CartDTO cart;
    CouponDTO coupon;
    SlotDTO slot;
    PaymentMode paymentMode;
    // ajout de StoreDTO
}
