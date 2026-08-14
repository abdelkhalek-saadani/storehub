package com.abdelkhalek.storehub.catalog.inventory.service;


import com.abdelkhalek.storehub.catalog.inventory.dto.ReservationItem;
import com.abdelkhalek.storehub.catalog.inventory.entity.Reservation;
import com.abdelkhalek.storehub.catalog.inventory.entity.StockEntity;
import com.abdelkhalek.storehub.catalog.inventory.dto.Item;
import com.abdelkhalek.storehub.catalog.inventory.exception.NoReservationsForSuchOrderException;
import com.abdelkhalek.storehub.catalog.inventory.mapper.StockMapper;
import com.abdelkhalek.storehub.catalog.inventory.entity.StockMovement;
import com.abdelkhalek.storehub.catalog.inventory.domain.*;
import com.abdelkhalek.storehub.catalog.inventory.enums.MovementType;
import com.abdelkhalek.storehub.catalog.inventory.enums.ReservationStatus;
import com.abdelkhalek.storehub.catalog.inventory.repository.ReservationRepository;
import com.abdelkhalek.storehub.catalog.inventory.repository.StockMovementRepository;
import com.abdelkhalek.storehub.catalog.inventory.repository.StockRepository;
import com.abdelkhalek.storehub.catalog.pricing.exception.StockNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    public static final int MAX_RETRIES = 3;
    public static final Duration RESERVATION_HOLD_TTL = Duration.ofMinutes(15);

    private final StockRepository stockRepository;
    private final ReservationRepository reservationRepository;
    private final StockMovementRepository stockMovementRepository;
    private final StockMapper stockMapper;

    @Autowired
    @Lazy
    private StockService self;

    /**
     * Check the availability if a given list of items( productId, qty)
     *
     * @param storeId store to check the products availability in
     * @param items   the items to check
     * @return true if there is sufficient stock, false otherwise
     */
    public Boolean checkStock(UUID storeId, List<Item> items) {
        for (Item item : items) {
            StockEntity stockEntity = stockRepository
                    .findByStoreIdAndProductId(storeId, item.productId())
                    .orElseThrow(() -> new StockNotFoundException(item.productId()));
            log.debug("Checking stock: {}", stockEntity);
            if (stockEntity.getQuantityAvailable() < item.quantity()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Called synchronously at checkout (POST /api/inventory/reservations).
     * All-or-nothing across the cart: if any line item is short, the whole
     * transaction rolls back and InsufficientStockException propagates.
     * <p>
     * Retry is at the WHOLE-METHOD level, not per row: if the transaction
     * fails because another checkout won the race on one of the rows (so we have a stale view
     * but there still is available stock to reserve it) , we
     * redo the entire attempt against fresh data rather than trying to
     * patch a half-applied transaction.
     *
     * @return the list of reservations ids
     */
    public List<UUID> reserveForOrder(UUID storeId, List<ReservationItem> items) {
        int attempt = 0;
        while (true) {
            try {
                return self.reserveForOrderTx(storeId, items);
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
    public List<UUID> reserveForOrderTx(UUID storeId, List<ReservationItem> items) {
        Instant expiresAt = Instant.now().plus(RESERVATION_HOLD_TTL); // payment hold TTL

        List<UUID> reservationIds = new ArrayList<>();
        for (ReservationItem item : items) {

            StockEntity stockEntity = stockRepository.findByStoreIdAndProductId(storeId,
                            item.productId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "No stock row for product " + item.productId()));

            Stock stock = stockMapper.toDomain(stockEntity);
            stock.reserve(item.quantity()); // throws InsufficientStockException if short
            stockMapper.applyTo(stock, stockEntity);

            stockRepository.save(stockEntity);

            Reservation reservation = new Reservation(
                    storeId, item.productId(), item.quantity(), expiresAt);
            Reservation r = reservationRepository.save(reservation);
            reservationIds.add(r.getId());
        }
        return reservationIds;
    }

    /**
     * <b>Note: </b>Not used in the codebase yet, will be wired to order shipped event
     * <p>
     * Called when order-service consumes PaymentSucceeded.
     */
    @Transactional
    public void confirmForOrder(UUID storeId, UUID orderId) {
        List<Reservation> active = reservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.ACTIVE);

        if (active.isEmpty()) {
            throw new NoReservationsForSuchOrderException(
                    "No active reservation found for order " + orderId + " in store " + storeId);
        }

        for (Reservation reservation : active) {
            StockEntity stockEntity = stockRepository
                    .findByStoreIdAndProductId(storeId, reservation.getProductId())
                    .orElseThrow();
            Stock stock = stockMapper.toDomain(stockEntity);
            stock.confirmDeduction(reservation.getQuantity());
            stockMapper.applyTo(stock, stockEntity);
            stockRepository.save(stockEntity);

            stockMovementRepository.save(new StockMovement(
                    storeId, reservation.getProductId(), MovementType.DEDUCT,
                    -reservation.getQuantity(), orderId, "payment succeeded"));

            reservation.confirm();
            reservationRepository.save(reservation);
        }
    }

    /**
     * Release items reservations by their reservation ids (Called when payment failed is
     * consumed or on explicit cancellation)
     * <p>
     * Throws {@code NoSuchElementException } if no active reservations is found
     */
    @Transactional
    public void releaseItems(List<UUID> reservationIds) {
        List<Reservation> active = reservationRepository
                .findAllById(reservationIds);

        UUID storeId = active.getFirst().getStoreId();

        releaseItems(active, storeId);
    }

    /**
     * <b>Note:</b> Not used anywhere in the codebase, kept for convenience
     * <p>
     * Called when order-service consumes PaymentFailed, or on explicit cancellation.
     */
    @Transactional
    public void releaseItems(UUID storeId, UUID orderId) {
        List<Reservation> active = reservationRepository
                .findByOrderIdAndStatus(orderId, ReservationStatus.ACTIVE);

        releaseItems(active, storeId);
    }

    /**
     * Helper method for releaseItems by reservationIds/orderId
     */
    private void releaseItems(List<Reservation> reservations, UUID storeId) {
        for (Reservation reservation : reservations) {
            StockEntity stockEntity = stockRepository
                    .findByStoreIdAndProductId(storeId, reservation.getProductId())
                    .orElseThrow();

            Stock stock = stockMapper.toDomain(stockEntity);
            stock.release(reservation.getQuantity());
            stockMapper.applyTo(stock, stockEntity);
            stockRepository.save(stockEntity);

            reservation.release();
            reservationRepository.save(reservation);
        }
    }

    /**
     * <b>Note:</b> Not used yet in the codebase, this will be wired to a @Scheduled
     * method to handle dead reservations
     * <p>
     * Scheduled sweep for reservations nobody explicitly resolved (abandoned
     * payment, lost PaymentFailed message, etc.).
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
            stockRepository.save(stockEntity);

            reservation.expire();
            reservationRepository.save(reservation);
        }
    }
}
