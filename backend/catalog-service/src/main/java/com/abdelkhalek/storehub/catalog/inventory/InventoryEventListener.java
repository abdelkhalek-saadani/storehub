package com.abdelkhalek.storehub.catalog.inventory;

import com.abdelkhalek.storehub.catalog.inventory.dto.ItemsReleaseEvent;
import com.abdelkhalek.storehub.catalog.shared.event.OrderCreateEvent;
import com.abdelkhalek.storehub.catalog.inventory.service.ReservationService;
import com.abdelkhalek.storehub.catalog.inventory.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventListener {

    private final StockService stockService;
    private final ReservationService reservationService;

    @RabbitListener(queues = "items.released.queue")
    public void handleItemsReleased(ItemsReleaseEvent event) {
        log.debug("Received ItemsReleased event: {}", event);
        stockService.releaseItems(event.retainIds());
    }

    @RabbitListener(queues = "inventory.order.created.queue")
    public void handleOrderCreated(OrderCreateEvent event) {
        log.debug("Received OrderCreated event: {}", event);
        reservationService.setReservationsOrderId(event.inventoryRetainIds(), event.orderId());
    }

}
