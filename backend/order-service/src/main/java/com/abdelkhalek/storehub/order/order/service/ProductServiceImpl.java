package com.abdelkhalek.storehub.order.order.service;

import com.abdelkhalek.storehub.order.cart.entities.CartEntity;
import com.abdelkhalek.storehub.order.cart.services.cart.CartRepository;
import com.abdelkhalek.storehub.order.order.OrderEventPublisher;
import com.abdelkhalek.storehub.order.order.dto.AvailabilityRequest;
import com.abdelkhalek.storehub.order.order.dto.CartItemRequest;
import com.abdelkhalek.storehub.order.order.dto.RetainRequest;
import com.abdelkhalek.storehub.order.order.mapper.CartItemRequestMapper;
import com.abdelkhalek.storehub.order.order.spi.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service("orderProductServiceImpl")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductClient productClient;

    @Autowired
    CartItemRequestMapper cartItemRequestMapper;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderEventPublisher orderEventPublisher;

    @Override
    public Mono<Boolean> checkAvailability(UUID storeId, UUID cartId) {
        Mono<CartEntity> cartEntityMono = cartRepository.findById(cartId);
        return cartEntityMono.flatMap(cartEntity -> {
            List<CartItemRequest> items =
                    cartItemRequestMapper.fromCartItemEntities(cartEntity.getItems());
            AvailabilityRequest request = new AvailabilityRequest(items, storeId);
            return productClient.getAvailability(request);
        });
    }

    @Override
    public Mono<List<UUID>> retain(UUID storeId, UUID cartId) {
        Mono<CartEntity> cartEntityMono = cartRepository.findById(cartId);
        return cartEntityMono.flatMap(cartEntity -> {
            List<CartItemRequest> items =
                    cartItemRequestMapper.fromCartItemEntities(cartEntity.getItems());
            RetainRequest request = new RetainRequest(items, storeId);
            return productClient.retain(request);
        });
    }

    @Override
    public Mono<Void> release(List<UUID> retainIds) {
        log.info("Releasing the items using the retain id {}...", retainIds);
        return orderEventPublisher.itemsReleased(retainIds);
    }
}
