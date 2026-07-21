package com.abdelkhalek.storehub.order.infrastructure.models.pricing;

import com.abdelkhalek.storehub.order.order.models.Money;
import lombok.Data;

import java.util.List;

@Data
public class PriceResponse {

    List<CartItemResponse> items;
    Money total;

}
