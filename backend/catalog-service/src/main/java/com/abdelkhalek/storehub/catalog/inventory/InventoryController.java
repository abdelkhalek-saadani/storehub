package com.abdelkhalek.storehub.catalog.inventory;

import com.abdelkhalek.storehub.catalog.shared.dto.AvailabilityResponse;
import com.abdelkhalek.storehub.catalog.inventory.dto.Item;
import com.abdelkhalek.storehub.catalog.inventory.dto.ReservationResponse;
import com.abdelkhalek.storehub.catalog.inventory.dto.ReservationItem;
import com.abdelkhalek.storehub.catalog.inventory.service.StockService;
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
public class InventoryController {

    private final StockService stockService;

    @PostMapping("check-availability")
    ResponseEntity<AvailabilityResponse> checkAvailability(
            @RequestParam UUID storeId,
            @RequestBody List<Item> items
    ) {
        log.debug("Received items at check-availability: {}", items);
        return ResponseEntity.ok(new AvailabilityResponse(stockService.checkStock(storeId, items)));
    }

    @PostMapping("reservations")
    ResponseEntity<ReservationResponse> reservations(
            @RequestParam UUID storeId,
            @RequestBody List<Item> items
    ) {
        List<ReservationItem> reservationItems =
                items.stream().map(item -> new ReservationItem(item.productId(), item.quantity()))
                        .toList();
        List<UUID> ids = stockService.reserveForOrder(storeId, null, reservationItems);
        return ResponseEntity.ok().body(new ReservationResponse(ids));
    }

}
