package com.abdelkhalek.storehub.catalog.inventory;

import com.abdelkhalek.storehub.catalog.inventory.dto.ItemsReleaseEvent;
import com.abdelkhalek.storehub.catalog.inventory.service.StockService;
import com.abdelkhalek.storehub.catalog.store.StoreCreatedEvent;
import com.abdelkhalek.storehub.catalog.store.StoreShadowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final StockService stockService;

    @RabbitListener(queues = "items.released.queue")
    public void handleItemsReleased(ItemsReleaseEvent event) {
        stockService.releaseItems(event.retainIds());
    }
}
