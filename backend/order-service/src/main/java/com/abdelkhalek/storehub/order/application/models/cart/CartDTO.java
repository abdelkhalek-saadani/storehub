package com.abdelkhalek.storehub.order.application.models.cart;

import com.abdelkhalek.storehub.order.application.models.MoneyDTO;

import java.util.ArrayList;
import java.util.List;

public class CartDTO {
    private List<CartItemDTO> items = new ArrayList<>();
    private MoneyDTO total;
}