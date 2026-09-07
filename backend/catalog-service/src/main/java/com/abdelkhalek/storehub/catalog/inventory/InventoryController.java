package com.abdelkhalek.storehub.catalog.inventory;

import com.abdelkhalek.storehub.catalog.shared.dto.AvailabilityResponse;
import com.abdelkhalek.storehub.catalog.inventory.dto.Item;
import com.abdelkhalek.storehub.catalog.inventory.dto.ReservationResponse;
import com.abdelkhalek.storehub.catalog.inventory.dto.ReservationItem;
import com.abdelkhalek.storehub.catalog.inventory.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Stock availability checks and reservations")
public class InventoryController {

    private final StockService stockService;

    @Operation(summary = "Check stock availability for a list of items")
    @PostMapping("check-availability")
    ResponseEntity<AvailabilityResponse> checkAvailability(
            @RequestParam UUID storeId,
            @RequestBody List<Item> items
    ) {
        log.debug("Received items at check-availability: {}", items);
        return ResponseEntity.ok(new AvailabilityResponse(stockService.checkStock(storeId, items)));
    }

    @Operation(summary = "Reserve stock for an order")
    @ApiResponse(responseCode = "200", description = "Items reserved")
    @PostMapping("reservations")
    ResponseEntity<ReservationResponse> reservations(
            @RequestParam UUID storeId,
            @RequestBody List<Item> items
    ) {
        List<ReservationItem> reservationItems =
                items.stream().map(item -> new ReservationItem(item.productId(), item.quantity()))
                        .toList();
        List<UUID> ids = stockService.reserveForOrder(storeId, reservationItems);
        return ResponseEntity.ok().body(new ReservationResponse(ids));
    }

}
