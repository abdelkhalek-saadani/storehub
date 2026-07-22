package com.abdelkhalek.storehub.order.order.repository;

import com.abdelkhalek.storehub.order.order.entity.OrderEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface OrderReactiveRepository extends ReactiveCrudRepository<OrderEntity, UUID> {

}
