package com.abdelkhalek.storehub.order.order.dto;


import com.abdelkhalek.storehub.order.order.models.OrderStatus;

public record OrderStatusDto(String code, String label) {
    public static OrderStatusDto from(OrderStatus status) {
        return new OrderStatusDto(status.name(), status.getLabel());
    }
}