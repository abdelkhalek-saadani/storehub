package com.abdelkhalek.storehub.order.order.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Cart {
    private List<OrderItem> items = new ArrayList<>();
    private Money total;

}