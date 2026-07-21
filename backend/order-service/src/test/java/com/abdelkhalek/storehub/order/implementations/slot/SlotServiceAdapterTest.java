package com.abdelkhalek.storehub.order.implementations.slot;

import com.abdelkhalek.storehub.order.order.models.Delivery;
import com.abdelkhalek.storehub.order.order.models.Slot;
import com.abdelkhalek.storehub.order.order.models.Store;
import com.abdelkhalek.storehub.order.order.spi.EventPublisher;
import com.abdelkhalek.storehub.order.infrastructure.implementations.slot.SlotClient;
import com.abdelkhalek.storehub.order.infrastructure.implementations.slot.SlotServiceAdapter;
import com.abdelkhalek.storehub.order.infrastructure.mappers.DeliveryRequestMapper;
import com.abdelkhalek.storehub.order.infrastructure.mappers.SlotRequestMapper;
import com.abdelkhalek.storehub.order.infrastructure.mappers.StoreRequestMapper;
import com.abdelkhalek.storehub.order.infrastructure.models.StoreRequest;
import com.abdelkhalek.storehub.order.infrastructure.models.slot.DeliveryRequest;
import com.abdelkhalek.storehub.order.infrastructure.models.slot.SlotReleaseEvent;
import com.abdelkhalek.storehub.order.infrastructure.models.slot.SlotRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SlotServiceAdapterTest {

    @Mock
    private SlotClient slotClient;

    @Mock
    private DeliveryRequestMapper deliveryRequestMapper;

    @Mock
    private SlotRequestMapper slotRequestMapper;

    @Mock
    private StoreRequestMapper storeRequestMapper;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private SlotServiceAdapter slotServiceAdapter;

    private Store store;
    private StoreRequest storeRequest;
    private UUID retainId;
    private Delivery delivery;
    private DeliveryRequest deliveryRequest;
    private Slot slot;
    private SlotRequest slotRequest;


    @BeforeEach
    void setUp() {

        UUID storeId = UUID.randomUUID();

        store = new Store(storeId);
        storeRequest = new StoreRequest(storeId.toString());
        retainId = UUID.randomUUID();

        lenient().when(deliveryRequestMapper.fromDelivery(delivery)).thenReturn(deliveryRequest);
        lenient().when(slotRequestMapper.fromSlot(slot)).thenReturn(slotRequest);
        lenient().when(storeRequestMapper.fromStore(store)).thenReturn(storeRequest);
    }

    @Test
    void checkAvailability_ShouldReturnResultFromSlotClient() {
        // Arrange
        when(slotClient.getAvailability(
                deliveryRequest, slotRequest, storeRequest)
        )
                .thenReturn(Mono.just(true));

        // Act
        Mono<Boolean> result = slotServiceAdapter.checkAvailability(
                delivery, slot, store
        );

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(slotRequestMapper).fromSlot(slot);
        verify(storeRequestMapper).fromStore(store);
        verify(deliveryRequestMapper).fromDelivery(delivery);
        verify(slotClient).getAvailability( deliveryRequest, slotRequest, storeRequest);
    }

    @Test
    void checkAvailability_ShouldPropagateErrorFromSlotClient() {
        // Arrange
        RuntimeException testException = new RuntimeException("Test exception");
        when(slotClient.getAvailability(deliveryRequest, slotRequest, storeRequest)).thenReturn(Mono.error(testException));

        // Act
        Mono<Boolean> result = slotServiceAdapter.checkAvailability(delivery , slot, store);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error -> error.equals(testException))
                .verify();

        verify(deliveryRequestMapper).fromDelivery(delivery);
        verify(slotRequestMapper).fromSlot(slot);
        verify(storeRequestMapper).fromStore(store);
        verify(slotClient).getAvailability(deliveryRequest, slotRequest, storeRequest);
    }

    @Test
    void retain_ShouldReturnRetainIdFromSlotClient() {
        // Arrange
        when(slotClient.retain(deliveryRequest, slotRequest, storeRequest)).thenReturn(Mono.just(retainId));

        // Act
        Mono<UUID> result = slotServiceAdapter.retain(delivery, slot, store);

        // Assert
        StepVerifier.create(result)
                .expectNext(retainId)
                .verifyComplete();

        verify(deliveryRequestMapper).fromDelivery(delivery);
        verify(slotRequestMapper).fromSlot(slot);
        verify(storeRequestMapper).fromStore(store);
        verify(slotClient).retain(deliveryRequest, slotRequest, storeRequest);
    }

    @Test
    void retain_ShouldPropagateErrorFromSlotClient() {
        // Arrange
        RuntimeException testException = new RuntimeException("Test exception");
        when(slotClient.retain(deliveryRequest, slotRequest, storeRequest)).thenReturn(Mono.error(testException));

        // Act
        Mono<UUID> result = slotServiceAdapter.retain(delivery, slot, store);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error -> error.equals(testException))
                .verify();

        verify(deliveryRequestMapper).fromDelivery(delivery);
        verify(slotRequestMapper).fromSlot(slot);
        verify(storeRequestMapper).fromStore(store);
        verify(slotClient).retain(deliveryRequest, slotRequest, storeRequest);
    }

    @Test
    void release_ShouldPublishEventWithRetainId() {
        // Arrange
        doNothing().when(eventPublisher).publish(any(SlotReleaseEvent.class));          // eventPublisher.publish() will do nothing when any SlotReleaseEvent is passed, but still it is called with that argument
        // Act
        Mono<Void> result = slotServiceAdapter.release(retainId);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(eventPublisher).publish(argThat(event ->
                event instanceof SlotReleaseEvent &&
                        ((SlotReleaseEvent) event).getRetainId().equals(retainId.toString())
        ));

    }

    @Test
    void release_ShouldPropagateErrorFromEventPublisher() {
        // Arrange
        RuntimeException testException = new RuntimeException("Test exception");
        doThrow(testException).when(eventPublisher).publish(any(SlotReleaseEvent.class));

        // Act
        Mono<Void> result = slotServiceAdapter.release(retainId);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error -> error.equals(testException))
                .verify();

        verify(eventPublisher).publish(any(SlotReleaseEvent.class));
    }

}
