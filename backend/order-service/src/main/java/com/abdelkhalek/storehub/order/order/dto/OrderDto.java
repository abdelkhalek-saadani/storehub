package com.abdelkhalek.storehub.order.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDto
        (
                UUID orderId,
                UUID userId,
                String email,
                UUID storeId,
                BigDecimal originalTotal,
                BigDecimal finalTotal,
                BigDecimal totalDiscount,


                List<OrderItemDto> items,


                AddressDto deliveryAddress,
                String billingAddress,

                UUID slotId,

                BigDecimal deliveryFee,

                OrderStatusDto status,

                UUID paymentId,

                String paymentApprovalLink,

                LocalDateTime createdAt


        ) {
}
