package com.abdelkhalek.storehub.order.cart.domain;

import lombok.Data;

import java.util.Map;
@Data
public class Discount {
        private String id;
        private String productId;
        private Map<String, String> attributes;
        Cart cart;
}
