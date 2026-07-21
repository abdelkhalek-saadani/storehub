package com.abdelkhalek.storehub.order.order.service;

import com.abdelkhalek.storehub.order.order.OrderEventPublisher;
import com.abdelkhalek.storehub.order.order.spi.SlotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class SlotServiceImpl implements SlotService {
    @Autowired
    private SlotClient slotClient;

    @Autowired
    private OrderEventPublisher orderEventPublisher;

    @Override
    public Mono<Boolean> checkAvailability(UUID storeId, UUID slotId) {
        return slotClient.getAvailability(storeId,slotId);
    }

    @Override
    public Mono<UUID> retain(UUID storeId, UUID slotId) {
        return slotClient.retain(storeId,slotId);
    }

    @Override
    public Mono<Void> release(UUID retainId) {
        log.debug("Releasing the slot using the retain id {}...", retainId);
        return orderEventPublisher.slotReleased(retainId);
    }
}
