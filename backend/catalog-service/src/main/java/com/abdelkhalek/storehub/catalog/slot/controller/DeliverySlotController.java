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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Delivery Slots", description = "Browse, reserve, and manage delivery slots")
public class DeliverySlotController {

    private final DeliverySlotRepository deliverySlotRepository;
    private final SlotBookingService slotBookingService;
    private final StoreService storeService;
    private final SlotService slotService;

    @Operation(summary = "Get a delivery slot by ID")
    @ApiResponse(responseCode = "200", description = "Slot found")
    @ApiResponse(responseCode = "404", description = "Slot not found")
    @GetMapping("{slotId}")
    public ResponseEntity<DeliverySlot> get(@PathVariable UUID slotId) {
        DeliverySlot s = slotService.getById(slotId);
        if (s == null) {
            return ResponseEntity.notFound().build();
        }
        log.debug("Get delivery slot {}", s);
        return ResponseEntity.ok(s);
    }

    @Operation(summary = "List available slots for a store on a given date",
            description = "For today's date, only returns slots starting after the current time.")
    @ApiResponse(responseCode = "200", description = "Slots found")
    @ApiResponse(responseCode = "404", description = "No available slots for the given store/date")
    @GetMapping
    public ResponseEntity<List<SlotDto>> getAvailableSlots(
            @RequestParam UUID storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Optional<List<DeliverySlot>> slots;
        if (!date.isEqual(LocalDate.now())) slots =
                deliverySlotRepository.findByStoreIdAndSlotDateAndStatus(storeId, date, DeliverySlot.Status.OPEN);
        else slots = deliverySlotRepository.findByStoreIdAndSlotDateAndStatusAndStartTimeAfter(
                storeId,
                date,
                DeliverySlot.Status.OPEN,
                LocalDateTime.now());
        if (slots.isEmpty()) return ResponseEntity.notFound().build();
        List<SlotDto> slotDtos = slots.get().stream()
                .sorted(Comparator.comparing(DeliverySlot::getStartTime))
                .map(s -> new SlotDto(s.getId(),
                        slotService.extractSlotLabel(s.getStartTime(), s.getEndTime()))).toList();
        return ResponseEntity.ok(slotDtos);
    }

    @Operation(summary = "Check which days in a range have available slots",
            description = "If 'from'/'to' are omitted, a default range is used.")
    @GetMapping("check-days")
    public ResponseEntity<List<LocalDate>> checkDays(
            @RequestParam UUID storeId,
            @RequestParam(required = false) @DateTimeFormat(iso =
                    DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(slotService.checkDays(storeId, from, to));
    }

    @Operation(summary = "Check whether a specific slot is currently available for booking")
    @GetMapping("check-availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @RequestParam UUID storeId,
            @RequestParam UUID slotId) {
        return ResponseEntity.ok()
                .body(new AvailabilityResponse(slotBookingService.isAvailable(storeId, slotId)));
    }

    @Operation(summary = "Reserve a delivery slot")
    @ApiResponse(responseCode = "201", description = "Slot reserved")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public ReserveSlotResponse reserve(
            @RequestParam UUID storeId,
            @Valid @RequestBody ReserveSlotRequest request) {
        SlotReservation reservedSlot = slotBookingService.reserveSlot(storeId, request.slotId());
        return new ReserveSlotResponse(reservedSlot.getId());
    }

    @Operation(summary = "Release a previously reserved slot")
    @ApiResponse(responseCode = "204", description = "Reservation released")
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
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Manually override a slot's capacity or status",
            description = "Marks the slot as manually overridden so automated jobs will not modify it afterward.")
    @ApiResponse(responseCode = "200", description = "Slot updated")
    @ApiResponse(responseCode = "404", description = "Slot not found for this store")
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
