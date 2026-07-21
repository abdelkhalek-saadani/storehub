package com.abdelkhalek.storehub.catalog.slot;


import com.abdelkhalek.storehub.catalog.slot.dto.SlotReleaseEvent;
import com.abdelkhalek.storehub.catalog.slot.service.SlotBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SlotEventListener {

    private final SlotBookingService slotService;

    @RabbitListener(queues = "slot.released.queue")
    public void handleSlotReleased(SlotReleaseEvent event) {
        slotService.releaseReservation(event.retainId());
    }
}

