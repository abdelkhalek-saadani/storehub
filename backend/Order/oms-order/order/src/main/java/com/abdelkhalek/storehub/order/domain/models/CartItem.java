package com.abdelkhalek.storehub.order.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CartItem {
    private UUID productId;
    private int quantity;
    private Money subtotal;
    private Money unitPrice;
    private Money originalUnitPrice;




}