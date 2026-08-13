package com.abdelkhalek.storehub.catalog.inventory;

import com.abdelkhalek.storehub.catalog.inventory.domain.Stock;
import com.abdelkhalek.storehub.catalog.inventory.dto.Item;
import com.abdelkhalek.storehub.catalog.inventory.dto.ReservationItem;
import com.abdelkhalek.storehub.catalog.inventory.entity.Reservation;
import com.abdelkhalek.storehub.catalog.inventory.entity.StockEntity;
import com.abdelkhalek.storehub.catalog.inventory.exception.InsufficientStockException;
import com.abdelkhalek.storehub.catalog.inventory.mapper.StockMapper;
import com.abdelkhalek.storehub.catalog.inventory.repository.ReservationRepository;
import com.abdelkhalek.storehub.catalog.inventory.repository.StockRepository;
import com.abdelkhalek.storehub.catalog.inventory.service.StockService;
import com.abdelkhalek.storehub.catalog.pricing.exception.StockNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private StockMapper stockMapper;

    @InjectMocks
    private StockService stockService;

    @Test
    void checkStock_returnsTrue_whenAllItemsHaveSufficientStock() {
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Item item = new Item(productId, 5);
        StockEntity entity = new StockEntity();
        entity.setQuantityAvailable(10);

        when(stockRepository.findByStoreIdAndProductId(storeId, productId))
                .thenReturn(Optional.of(entity));

        assertThat(stockService.checkStock(storeId, List.of(item))).isTrue();
    }

    @Test
    void checkStock_returnsFalse_whenAnyItemHasInsufficientStock() {
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Item item = new Item(productId, 20);
        StockEntity entity = new StockEntity();
        entity.setQuantityAvailable(5);

        when(stockRepository.findByStoreIdAndProductId(storeId, productId))
                .thenReturn(Optional.of(entity));

        assertThat(stockService.checkStock(storeId, List.of(item))).isFalse();
    }

    @Test
    void checkStock_throwsStockNotFoundException_whenProductHasNoStockRow() {
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Item item = new Item(productId, 1);

        when(stockRepository.findByStoreIdAndProductId(storeId, productId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.checkStock(storeId, List.of(item)))
                .isInstanceOf(StockNotFoundException.class);
    }

    @Test
    void reserveForOrderTx_createsReservations_whenStockSufficient() {
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        ReservationItem item = new ReservationItem(productId, 5);
        StockEntity entity = new StockEntity();
        Stock stock = Stock.builder().productId(productId).quantityAvailable(10).build();

        when(stockRepository.findByStoreIdAndProductId(storeId, productId))
                .thenReturn(Optional.of(entity));
        when(stockMapper.toDomain(entity)).thenReturn(stock);
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        List<UUID> result = stockService.reserveForOrderTx(storeId, List.of(item));

        assertThat(result).hasSize(1);
        verify(stockRepository).save(entity);
    }

    @Test
    void reserveForOrderTx_setsCorrectExpiryTime() {
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        ReservationItem item = new ReservationItem(productId, 1);
        StockEntity entity = new StockEntity();
        Stock stock = Stock.builder().productId(productId).quantityAvailable(10).build();

        when(stockRepository.findByStoreIdAndProductId(storeId, productId))
                .thenReturn(Optional.of(entity));
        when(stockMapper.toDomain(entity)).thenReturn(stock);
        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        when(reservationRepository.save(captor.capture()))
                .thenAnswer(inv -> inv.getArgument(0));

        Instant before = Instant.now();
        stockService.reserveForOrderTx(storeId, List.of(item));
        Instant after = Instant.now();

        Instant expiresAt = captor.getValue().getExpiresAt();
        assertThat(expiresAt).isBetween(
                before.plus(StockService.RESERVATION_HOLD_TTL).minusSeconds(1),
                after.plus(StockService.RESERVATION_HOLD_TTL).plusSeconds(1));
    }

    @Test
    void reserveForOrderTx_throwsInsufficientStock_andSavesNothing_whenAnyItemShort() {
        UUID storeId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        ReservationItem item1 = new ReservationItem(productId1, 2);
        ReservationItem item2 = new ReservationItem(productId2, 100); // short

        StockEntity entity1 = new StockEntity();
        StockEntity entity2 = new StockEntity();
        Stock stock1 = Stock.builder().productId(productId1).quantityAvailable(10).build();
        Stock stock2 = Stock.builder().productId(productId2).quantityAvailable(5).build();

        when(stockRepository.findByStoreIdAndProductId(storeId, productId1))
                .thenReturn(Optional.of(entity1));
        when(stockRepository.findByStoreIdAndProductId(storeId, productId2))
                .thenReturn(Optional.of(entity2));
        when(stockMapper.toDomain(entity1)).thenReturn(stock1);
        when(stockMapper.toDomain(entity2)).thenReturn(stock2);
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        assertThatThrownBy(() ->
                stockService.reserveForOrderTx(storeId, List.of(item1, item2)))
                .isInstanceOf(InsufficientStockException.class);

        verify(reservationRepository, never()).save(argThat(r -> r.getProductId()
                .equals(productId2)));
    }

    @Test
    void reserveForOrderTx_throwsNoSuchElement_whenStockRowMissing() {
        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        ReservationItem item = new ReservationItem(productId, 1);

        when(stockRepository.findByStoreIdAndProductId(storeId, productId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.reserveForOrderTx(storeId, List.of(item)))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void reserveForOrderTx_handlesMultipleItems_correctly() {
        // Arrange
        UUID storeId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        ReservationItem item1 = new ReservationItem(productId1, 5);
        ReservationItem item2 = new ReservationItem(productId2, 10);

        StockEntity stockEntity1 = new StockEntity();
        StockEntity stockEntity2 = new StockEntity();
        Stock stockDomain1 = Stock.builder().quantityAvailable(5).productId(productId1).build();
        Stock stockDomain2 = Stock.builder().quantityAvailable(10).productId(productId2).build();

        when(stockRepository.findByStoreIdAndProductId(storeId, productId1))
                .thenReturn(Optional.of(stockEntity1));
        when(stockRepository.findByStoreIdAndProductId(storeId, productId2))
                .thenReturn(Optional.of(stockEntity2));
        when(stockMapper.toDomain(stockEntity1)).thenReturn(stockDomain1);
        when(stockMapper.toDomain(stockEntity2)).thenReturn(stockDomain2);
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(inv -> inv.getArgument(0));


        // Act
        List<UUID> result = stockService.reserveForOrderTx(storeId, List.of(item1, item2));

        // Assert
        assertThat(result).hasSize(2);
        verify(stockRepository).save(stockEntity1);
        verify(stockRepository).save(stockEntity2);
        ArgumentCaptor<Reservation> captor =
                ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository, times(2)).save(captor.capture());

        Reservation saved1 = captor.getAllValues().getFirst();
        assertThat(saved1.getStoreId()).isEqualTo(storeId);
        assertThat(saved1.getProductId()).isEqualTo(productId1);
        assertThat(saved1.getQuantity()).isEqualTo(item1.quantity());

        Reservation saved2 = captor.getAllValues().getLast();
        assertThat(saved2.getStoreId()).isEqualTo(storeId);
        assertThat(saved2.getProductId()).isEqualTo(productId2);
        assertThat(saved2.getQuantity()).isEqualTo(item2.quantity());
    }

    @Test
    void reserveForOrder_retriesOnOptimisticLockFailure_andSucceeds() {
        StockService spyService = spy(stockService);
        ReflectionTestUtils.setField(stockService, "self", spyService);

        UUID storeId = UUID.randomUUID();
        List<ReservationItem> items = List.of(new ReservationItem(UUID.randomUUID(), 1));
        List<UUID> expected = List.of(UUID.randomUUID());

        doThrow(new ObjectOptimisticLockingFailureException(StockEntity.class, "id"))
                .doReturn(expected)
                .when(spyService).reserveForOrderTx(storeId, items);

        List<UUID> result = stockService.reserveForOrder(storeId, items);

        assertThat(result).isEqualTo(expected);
        verify(spyService, times(2)).reserveForOrderTx(storeId, items);
    }

    @Test
    void reserveForOrder_throwsAfterMaxRetries_whenAlwaysConflicting() {
        StockService spyService = spy(stockService);
        ReflectionTestUtils.setField(stockService, "self", spyService);

        UUID storeId = UUID.randomUUID();
        List<ReservationItem> items = List.of(new ReservationItem(UUID.randomUUID(), 1));

        doThrow(new ObjectOptimisticLockingFailureException(StockEntity.class, "id"))
                .when(spyService).reserveForOrderTx(storeId, items);

        assertThatThrownBy(() -> stockService.reserveForOrder(storeId, items))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
        verify(spyService, times(3)).reserveForOrderTx(storeId, items); // MAX_RETRIES
    }

    @Test
    void reserveForOrder_doesNotRetry_onNonLockingException() {
        StockService spyService = spy(stockService);
        ReflectionTestUtils.setField(stockService, "self", spyService);

        UUID storeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int qty = 10;
        List<ReservationItem> items = List.of(new ReservationItem(productId, qty));

        doThrow(new InsufficientStockException(productId,qty,1))
                .when(spyService).reserveForOrderTx(storeId, items);

        assertThatThrownBy(() -> stockService.reserveForOrder(storeId, items))
                .isInstanceOf(InsufficientStockException.class);
        verify(spyService, times(1)).reserveForOrderTx(storeId, items);
    }
}
