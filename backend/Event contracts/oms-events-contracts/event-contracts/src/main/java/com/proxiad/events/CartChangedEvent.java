package com.proxiad.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartChangedEvent{
    private String cartId;
    private List<CartItemEvent> cartItems;

}