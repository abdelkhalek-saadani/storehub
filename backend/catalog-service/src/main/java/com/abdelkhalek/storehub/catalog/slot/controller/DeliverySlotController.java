package com.abdelkhalek.storehub.catalog.slot.controller;


import com.abdelkhalek.storehub.catalog.slot.dto.ReserveSlotRequest;
import com.abdelkhalek.storehub.catalog.slot.entity.DeliverySlot;
import com.abdelkhalek.storehub.catalog.slot.entity.SlotReservation;
import com.abdelkhalek.storehub.catalog.slot.repository.DeliverySlotRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Tenant identification: taken from a header set by the gateway/auth layer
 * after resolving the caller's store. In production, prefer extracting this
 * from a validated JWT claim rather than trusting a raw client header.
 */
@RestController
@RequestMapping("/api/delivery-slots")
@RequiredArgsConstructor
public class DeliverySlotController {

    private final DeliverySlotRepository deliverySlotRepository;
    private final SlotBookingService slotBookingService;

    @GetMapping
    public List<DeliverySlot> getAvailableSlots(
            @RequestParam UUID storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return deliverySlotRepository.findByStoreIdAndSlotDateBetweenAndStatus(
                storeId, from, to, DeliverySlot.Status.OPEN);
    }

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public SlotReservation reserve(
            @RequestParam UUID storeId,
            @Valid @RequestBody ReserveSlotRequest request) {
        return slotBookingService.reserveSlot(storeId, request.slotId(), request.cartId());
    }

    @PostMapping("/reservations/{reservationId}/release")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void release(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @PathVariable Long reservationId) {
        slotBookingService.releaseReservation(tenantId, reservationId);
    }

    /**
     * Owner exception-editing a single materialized occurrence (e.g. holiday
     * capacity cut). Sets manualOverride=true so neither the nightly
     * generation job nor a future config sync will ever touch this row again.
     */
    @PatchMapping("/{slotId}/override")
    public DeliverySlot manualOverride(
            @RequestHeader("X-Tenant-Id") Long tenantId,
            @PathVariable Long slotId,
            @RequestParam(required = false) Integer maxCapacity,
            @RequestParam(required = false) DeliverySlot.Status status) {

        DeliverySlot slot = deliverySlotRepository.findByIdAndTenantId(slotId, tenantId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Slot not found: " + slotId));

        if (maxCapacity != null) slot.setMaxCapacity(maxCapacity);
        if (status != null) slot.setStatus(status);
        slot.setManualOverride(true);

        return deliverySlotRepository.save(slot);
    }
}
