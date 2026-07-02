package com.abdelkhalek.storehub.order.cart;

import com.abdelkhalek.storehub.order.cart.entities.CartItemEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface CartItemRepository extends ReactiveCrudRepository<CartItemEntity, UUID> {

}