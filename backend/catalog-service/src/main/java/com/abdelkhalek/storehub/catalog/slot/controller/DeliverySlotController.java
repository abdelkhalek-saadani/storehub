package com.abdelkhalek.storehub.catalog.slot.controller;


import com.abdelkhalek.storehub.catalog.shared.dto.AvailabilityResponse;
import com.abdelkhalek.storehub.catalog.slot.dto.ReserveSlotRequest;
import com.abdelkhalek.storehub.catalog.slot.dto.ReserveSlotResponse;
import com.abdelkhalek.storehub.catalog.slot.dto.SlotDto;
import com.abdelkhalek.storehub.catalog.slot.entity.DeliverySlot;
import com.abdelkhalek.storehub.catalog.slot.entity.SlotReservation;
import com.abdelkhalek.storehub.catalog.slot.repository.DeliverySlotRepository;
import com.abdelkhalek.storehub.catalog.slot.service.SlotBookingService;
import com.abdelkhalek.storehub.catalog.slot.service.SlotService;
import com.abdelkhalek.storehub.catalog.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/delivery-slots")
@RequiredArgsConstructor
public class DeliverySlotController {

    private final DeliverySlotRepository deliverySlotRepository;
    private final SlotBookingService slotBookingService;
    private final StoreService storeService;
    private final SlotService slotService;

    @GetMapping("{slotId}")
    public ResponseEntity<DeliverySlot> get(@PathVariable UUID slotId) {
        DeliverySlot s = slotService.getById(slotId);
        if (s == null) {
            return ResponseEntity.notFound().build();
        }
        log.debug("Get delivery slot {}", s);
        return ResponseEntity.ok(s);
    }

    @GetMapping
    public ResponseEntity<List<SlotDto>> getAvailableSlots(
            @RequestParam UUID storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Optional<List<DeliverySlot>> slots;
        if (!date.isEqual(LocalDate.now())) slots =
                deliverySlotRepository.findByStoreIdAndSlotDate(storeId, date);
        else slots = deliverySlotRepository.findByStoreIdAndSlotDateAndStartTimeAfter(
                storeId,
                date,
                LocalDateTime.now());
        if (slots.isEmpty()) return ResponseEntity.notFound().build();
        List<SlotDto> slotDtos = slots.get().stream()
                .sorted(Comparator.comparing(DeliverySlot::getStartTime))
                .map(s -> new SlotDto(s.getId(),
                        slotService.extractSlotLabel(s.getStartTime(), s.getEndTime()))).toList();
        return ResponseEntity.ok(slotDtos);
    }

    @GetMapping("check-days")
    public ResponseEntity<List<LocalDate>> checkDays(
            @RequestParam UUID storeId,
            @RequestParam(required = false) @DateTimeFormat(iso =
                    DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(slotService.checkDays(storeId, from, to));
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
