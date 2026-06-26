package com.abdelkhalek.storehub.order.domain.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Cart {
    private List<CartItem> items = new ArrayList<>();
    private Money total;

}