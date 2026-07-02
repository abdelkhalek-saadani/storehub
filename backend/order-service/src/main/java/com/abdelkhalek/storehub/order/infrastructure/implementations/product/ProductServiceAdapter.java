package com.abdelkhalek.storehub.order.infrastructure.implementations.product;

import com.abdelkhalek.storehub.order.domain.models.CartItem;
import com.abdelkhalek.storehub.order.domain.models.Store;
import com.abdelkhalek.storehub.order.domain.spi.EventPublisher;
import com.abdelkhalek.storehub.order.domain.spi.ProductService;
import com.abdelkhalek.storehub.order.infrastructure.mappers.CartItemRequestMapper;
import com.abdelkhalek.storehub.order.infrastructure.mappers.StoreRequestMapper;
import com.abdelkhalek.storehub.order.infrastructure.models.product.ItemsReleaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service("orderProductServiceAdapter")
public class ProductServiceAdapter implements ProductService {

    @Autowired
    private ProductClient productClient;

    @Autowired
    CartItemRequestMapper cartItemRequestMapper;

    @Autowired
    StoreRequestMapper storeRequestMapper;

    @Autowired
    EventPublisher eventPublisher;

    @Override
    public Mono<Boolean> checkAvailability(List<CartItem> items, Store store) {
        return productClient.getAvailability(
                cartItemRequestMapper.fromCartItems(items),
                storeRequestMapper.fromStore(store)
        );
    }

    @Override
    public Mono<UUID> retain(List<CartItem> items, Store store) {
        return productClient.retain(
                cartItemRequestMapper.fromCartItems(items),
                storeRequestMapper.fromStore(store)
        );
    }

    @Override
    public Mono<Void> release(UUID retainId) {
        log.info("Releasing the items using the retain id {}...", retainId);
        return Mono.fromCallable(() -> eventPublisher.publish(new ItemsReleaseEvent(retainId.toString())));
    }
}
