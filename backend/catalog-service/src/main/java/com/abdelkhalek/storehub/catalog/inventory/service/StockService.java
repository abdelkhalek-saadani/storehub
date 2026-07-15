package com.abdelkhalek.storehub.catalog.inventory.service;


import com.abdelkhalek.storehub.catalog.inventory.Reservation;
import com.abdelkhalek.storehub.catalog.inventory.StockEntity;
import com.abdelkhalek.storehub.catalog.inventory.mapper.StockMapper;
import com.abdelkhalek.storehub.catalog.inventory.StockMovement;
import com.abdelkhalek.storehub.catalog.inventory.domain.*;
import com.abdelkhalek.storehub.catalog.inventory.enums.MovementType;
import com.abdelkhalek.storehub.catalog.inventory.enums.ReservationStatus;
import com.abdelkhalek.storehub.catalog.inventory.repository.ReservationRepository;
import com.abdelkhalek.storehub.catalog.inventory.repository.StockMovementRepository;
import com.abdelkhalek.storehub.catalog.inventory.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {

    private static final int MAX_RETRIES = 3;

    private final StockRepository stockRepository;
    private final ReservationRepository reservationRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockMapper stockMapper;

    @Autowired
    @Lazy
    private StockService self;

    /**
     * Called synchronously at checkout (POST /catalog/reservations).
     * All-or-nothing across the cart: if any line item is short, the whole
     * transaction rolls back and InsufficientStockException propagates.
     * <p>
     * Retry is at the WHOLE-METHOD level, not per row: if the transaction
     * fails because another checkout won the race on one of the rows (so we have a stale view
     * but there still is available stock to reserve it) , we
     * redo the entire attempt against fresh data rather than trying to
     * patch a half-applied transaction.
     */
    public void reserveForOrder(UUID storeId, UUID orderId, List<ReservationItem> items) {
        int attempt = 0;
        while (true) {
            try {
                self.reserveForOrderTx(storeId, orderId, items);
                return;
            } catch (ObjectOptimisticLockingFailureException ex) {
                attempt++;
                if (attempt >= MAX_RETRIES) {
                    throw ex;
                }
                // no sleep/backoff needed at this contention level for now
            }
        }
    }

    @Transactional
    protected void reserveForOrderTx(UUID storeId, UUID orderId, List<ReservationItem> items) {
        Instant expiresAt = Instant.now().plus(Duration.ofMinutes(15)); // checkout hold TTL

        for (ReservationItem item : items) {

            StockEntity stockEntity = stockRepository.findByStoreIdAndProductId(storeId,
                            item.productId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "No stock row for product " + item.productId()));

            Stock stock = stockMapper.toDomain(stockEntity);
            stock.reserve(item.quantity()); // throws InsufficientStockException if short
            stockMapper.applyTo(stock, stockEntity);

            //stockRepository.save(stock);

            Reservation reservation = new Reservation(
                    storeId, orderId, item.productId(), item.quantity(), expiresAt);
            reservationRepository.save(reservation);
        }
    }

    /**
     * Called when order-service consumes PaymentSucceeded.
     */
    @Transactional
    public void confirmForOrder(UUID storeId, UUID orderId) {
        List<Reservation> active = reservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.ACTIVE);

        for (Reservation reservation : active) {
            StockEntity stockEntity = stockRepository
                    .findByStoreIdAndProductId(storeId, reservation.getProductId())
                    .orElseThrow();
            Stock stock = stockMapper.toDomain(stockEntity);
            stock.confirmDeduction(reservation.getQuantity());
            stockMapper.applyTo(stock, stockEntity);
            // stockRepository.save(stock);

            stockMovementRepository.save(new StockMovement(
                    storeId, reservation.getProductId(), MovementType.DEDUCT,
                    -reservation.getQuantity(), orderId, "payment succeeded"));

            reservation.confirm();
            //reservationRepository.save(reservation);
        }
    }

    /**
     * Called when order-service consumes PaymentFailed, or on explicit cancellation.
     */
    @Transactional
    public void releaseForOrder(UUID storeId, UUID orderId) {
        List<Reservation> active = reservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.ACTIVE);

        for (Reservation reservation : active) {
            StockEntity stockEntity = stockRepository
                    .findByStoreIdAndProductId(storeId, reservation.getProductId())
                    .orElseThrow();

            Stock stock = stockMapper.toDomain(stockEntity);
            stock.release(reservation.getQuantity());
            stockMapper.applyTo(stock, stockEntity);
            //stockRepository.save(stock);

            reservation.release();
            //reservationRepository.save(reservation);
        }
    }

    /**
     * Scheduled sweep for reservations nobody explicitly resolved (abandoned
     * checkout, lost PaymentFailed message, etc). Wire this to a @Scheduled
     * method or a delayed-message consumer - either is defensible.
     */
    @Transactional
    public void expireAbandonedReservations() {
        List<Reservation> expired = reservationRepository
                .findByStatusAndExpiresAtBefore(ReservationStatus.ACTIVE, Instant.now());

        for (Reservation reservation : expired) {
            StockEntity stockEntity = stockRepository
                    .findByStoreIdAndProductId(reservation.getStoreId(), reservation.getProductId())
                    .orElseThrow();

            Stock stock = stockMapper.toDomain(stockEntity);
            stock.release(reservation.getQuantity());
            stockMapper.applyTo(stock, stockEntity);
            //stockRepository.save(stock);

            reservation.expire();
            //reservationRepository.save(reservation);
        }
    }
}
