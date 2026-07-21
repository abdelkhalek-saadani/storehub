package com.abdelkhalek.storehub.order.order.models;

public sealed interface OrderResponse permits CashOrderResponse, OnlineOrderResponse {
}
