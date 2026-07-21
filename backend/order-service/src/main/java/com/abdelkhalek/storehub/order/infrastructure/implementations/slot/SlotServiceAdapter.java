package com.abdelkhalek.storehub.order.infrastructure.implementations.slot;

import com.abdelkhalek.storehub.order.order.models.Delivery;
import com.abdelkhalek.storehub.order.order.models.Slot;
import com.abdelkhalek.storehub.order.order.models.Store;
import com.abdelkhalek.storehub.order.order.spi.EventPublisher;
import com.abdelkhalek.storehub.order.order.spi.SlotService;
import com.abdelkhalek.storehub.order.infrastructure.mappers.DeliveryRequestMapper;
import com.abdelkhalek.storehub.order.infrastructure.mappers.SlotRequestMapper;
import com.abdelkhalek.storehub.order.infrastructure.mappers.StoreRequestMapper;
import com.abdelkhalek.storehub.order.infrastructure.models.slot.SlotReleaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class SlotServiceAdapter implements SlotService {
    @Autowired
    private SlotClient slotClient;

    @Autowired
    private DeliveryRequestMapper deliveryRequestMapper;

    @Autowired
    private SlotRequestMapper slotRequestMapper;

    @Autowired
    private StoreRequestMapper storeRequestMapper;

    @Autowired
    private EventPublisher eventPublisher;

    @Override
    public Mono<Boolean> checkAvailability(Delivery delivery, Slot slot, Store store) {
        return slotClient.getAvailability(
                deliveryRequestMapper.fromDelivery(delivery),
                slotRequestMapper.fromSlot(slot),
                storeRequestMapper.fromStore(store)
        );
    }

    @Override
    public Mono<UUID> retain(Delivery delivery, Slot slot, Store store) {
        return slotClient.retain(
                deliveryRequestMapper.fromDelivery(delivery),
                slotRequestMapper.fromSlot(slot),
                storeRequestMapper.fromStore(store)
        );
    }

    @Override
    public Mono<Void> release(UUID retainId) {
        log.info("Releasing the slot using the retain id {}...", retainId);
        return Mono.fromCallable(() -> eventPublisher.publish(new SlotReleaseEvent(retainId.toString())));
    }
}
