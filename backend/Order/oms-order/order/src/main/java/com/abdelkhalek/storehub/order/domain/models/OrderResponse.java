package com.abdelkhalek.storehub.order.domain.models;

public sealed interface OrderResponse permits CashOrderResponse, OnlineOrderResponse {
}
