package com.abdelkhalek.storehub.order.infrastructure.implementations;

import com.abdelkhalek.storehub.order.infrastructure.models.order.OrderEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

//@Component
public interface OrderReactiveRepository extends ReactiveCrudRepository<OrderEntity, UUID> {

}
