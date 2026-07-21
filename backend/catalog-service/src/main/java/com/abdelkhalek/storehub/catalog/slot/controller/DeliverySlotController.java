package com.abdelkhalek.storehub.catalog.slot.controller;


import com.abdelkhalek.storehub.catalog.dtos.AvailabilityResponse;
import com.abdelkhalek.storehub.catalog.slot.dto.ReserveSlotRequest;
import com.abdelkhalek.storehub.catalog.slot.dto.ReserveSlotResponse;
import com.abdelkhalek.storehub.catalog.slot.entity.DeliverySlot;
import com.abdelkhalek.storehub.catalog.slot.entity.SlotReservation;
import com.abdelkhalek.storehub.catalog.slot.repository.DeliverySlotRepository;
import com.abdelkhalek.storehub.catalog.slot.service.SlotBookingService;
import com.abdelkhalek.storehub.catalog.store.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-slots")
@RequiredArgsConstructor
public class DeliverySlotController {

    private final DeliverySlotRepository deliverySlotRepository;
    private final SlotBookingService slotBookingService;
    private final StoreService storeService;

    @GetMapping
    public List<DeliverySlot> getAvailableSlots(
            @RequestParam UUID storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return deliverySlotRepository.findByStoreIdAndSlotDateBetweenAndStatus(
                storeId, from, to, DeliverySlot.Status.OPEN);
    }

    @GetMapping("check-availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @RequestParam UUID storeId,
            @RequestParam UUID slotId) {
        return ResponseEntity.ok()
                .body(new AvailabilityResponse(slotBookingService.isAvailable(storeId, slotId)));
    }

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public ReserveSlotResponse reserve(
            @RequestParam UUID storeId,
            @Valid @RequestBody ReserveSlotRequest request) {
        SlotReservation reservedSlot = slotBookingService.reserveSlot(storeId, request.slotId());
        return new ReserveSlotResponse(reservedSlot.getId());
    }

    @PostMapping("/reservations/{reservationId}/release")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void release(
            @RequestParam UUID storeId,
            @PathVariable UUID reservationId) {
        slotBookingService.releaseReservation(reservationId);
    }

    /*
     * Owner exception-editing a single materialized occurrence (e.g. holiday
     * capacity cut). Sets manualOverride=true so neither the nightly
     * generation job nor a future config sync will ever touch this row again.
     */
    @PatchMapping("/{slotId}/override")
    public DeliverySlot manualOverride(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID slotId,
            @RequestParam(required = false) Integer maxCapacity,
            @RequestParam(required = false) DeliverySlot.Status status) {
        UUID storeId = storeService.getStoreId(jwt.getSubject());
        DeliverySlot slot = deliverySlotRepository.findByIdAndStoreId(slotId, storeId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Slot not found: " + slotId));

        if (maxCapacity != null) slot.setMaxCapacity(maxCapacity);
        if (status != null) slot.setStatus(status);
        slot.setManualOverride(true);

        return deliverySlotRepository.save(slot);
    }
}
