package com.abdelkhalek.storehub.order.order.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderDto
        (
                UUID orderId,
                UUID userId,
                UUID storeId,
                BigDecimal originalTotal,
                BigDecimal finalTotal,
                BigDecimal totalDiscount,


                List<OrderItemDto> items,


                String deliveryAddress,
                String billingAddress,

                UUID slotId,

                BigDecimal deliveryFee,

                OrderStatusDto status,

                UUID paymentId,

                String paymentApprovalLink


        ) {
}
