package com.abdelkhalek.storehub.order.order.repository;

import com.abdelkhalek.storehub.order.order.entity.OrderEntity;
import com.abdelkhalek.storehub.order.order.mapper.OrderMapper;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.spi.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderReactiveRepository orderReactiveRepository;
    private final OrderMapper orderMapper;

    @Override
    public Mono<Order> save(Order order) {
        log.debug("Saving Order: {}", order);
        Mono<OrderEntity> orderEntity = orderReactiveRepository.save(orderMapper.toEntity(order));
        return orderEntity.map(orderMapper::fromEntity);
    }

}
