package com.abdelkhalek.storehub.catalog.slot;


import com.abdelkhalek.storehub.catalog.shared.event.OrderCreateEvent;
import com.abdelkhalek.storehub.catalog.slot.dto.SlotReleaseEvent;
import com.abdelkhalek.storehub.catalog.slot.service.SlotBookingService;
import com.abdelkhalek.storehub.catalog.slot.service.SlotReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class SlotEventListener {

    private final SlotBookingService slotService;
    private final SlotReservationService slotReservationService;

    @RabbitListener(queues = "slot.released.queue")
    public void handleSlotReleased(SlotReleaseEvent event) {
        log.debug("Received SlotReleased event: {}", event);
        slotService.releaseReservation(event.retainId());
    }

    @RabbitListener(queues = "slot.order.created.queue")
    public void handleOrderCreated(OrderCreateEvent event) {
        log.debug("Received OrderCreated event: {}", event);
        slotReservationService.setReservationOrderId(event.slotRetainId(), event.orderId());
    }

}

