package com.abdelkhalek.storehub.order.infrastructure.implementations;

import com.abdelkhalek.storehub.order.domain.models.Order;
import com.abdelkhalek.storehub.order.domain.spi.OrderRepository;
import com.abdelkhalek.storehub.order.infrastructure.mappers.OrderEntityMapper;
import com.abdelkhalek.storehub.order.infrastructure.models.order.OrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderReactiveRepository orderReactiveRepository;
    private final OrderEntityMapper orderEntityMapper;

    @Override
    public Mono<Order> save(Order order) {
        log.info("order total value : {}", order.getTotal());
        Mono<OrderEntity> orderEntity = orderReactiveRepository.save(orderEntityMapper.fromOrder(order));
        return orderEntity.map(orderEntityMapper::toOrder);
    }

}
