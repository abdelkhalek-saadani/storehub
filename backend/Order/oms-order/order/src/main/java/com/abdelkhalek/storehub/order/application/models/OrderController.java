package com.abdelkhalek.storehub.order.application.models;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing orders (Gestion des commandes)
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "Endpoints for managing customer orders")
public class OrderController {

    // US2.1: Validate cart and place an order
    @Operation(
            summary = "Place order",
            description = "Allows a user to confirm their cart by providing delivery address, shipping method, and delivery time slot."
    )
    @ApiResponse(responseCode = "201", description = "Order created successfully")
    @PostMapping
    public ResponseEntity<String> placeOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Order request details including address, shipping method, and delivery slot",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PlaceOrderRequest.class))
            )
            @org.springframework.web.bind.annotation.RequestBody PlaceOrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body("Order created");
    }

    // Get full details of a single order
    @Operation(
            summary = "Get order details",
            description = "Retrieve full details of a specific order."
    )
    @ApiResponse(responseCode = "200", description = "Order details retrieved successfully")
    @GetMapping("/{orderId}")
    public ResponseEntity<String> getOrderDetails(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok("Order details");
    }

    // US2.2: Cancel order by customer
    @Operation(
            summary = "Cancel order (customer)",
            description = "Allows a customer to cancel an order if it hasn't been delivered."
    )
    @ApiResponse(responseCode = "200", description = "Order cancelled successfully")
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrderByCustomer(
            @Parameter(description = "ID of the order to cancel", required = true)
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok("Order cancelled");
    }

    // US2.3: Track order status
    @Operation(
            summary = "Track order status",
            description = "Retrieve the current status of a customer's order (e.g., preparing, shipped, delivered)."
    )
    @ApiResponse(responseCode = "200", description = "Order status retrieved successfully")
    @GetMapping("/{orderId}/status")
    public ResponseEntity<String> getOrderStatus(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok("Order status");
    }

    // US2.4: Get all orders (admin)
    @Operation(
            summary = "Get all orders (admin)",
            description = "Allows administrators to view all placed orders."
    )
    @ApiResponse(responseCode = "200", description = "List of all orders")
    @GetMapping
    public ResponseEntity<List<String>> getAllOrders() {
        return ResponseEntity.ok(List.of("Order1", "Order2"));
    }


    // US2.5: Cancel order and refund (admin)
    @Operation(
            summary = "Cancel and refund order (admin)",
            description = "Allows an administrator to cancel a customer's order and process a refund."
    )
    @ApiResponse(responseCode = "200", description = "Order cancelled and refunded successfully")
    @PatchMapping("/{orderId}/admin-cancel")
    public ResponseEntity<String> cancelOrderByAdmin(
            @Parameter(description = "ID of the order to cancel", required = true)
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok("Order cancelled and refunded");
    }

    // --- Request DTO for placing orders ---
    public static class PlaceOrderRequest {
        @Schema(description = "Delivery address", example = "123 Main St, Paris")
        public String deliveryAddress;

        @Schema(description = "Delivery method", example = "Express")
        public String deliveryMethod;

        @Schema(description = "Preferred delivery time slot", example = "2025-09-05T14:00-16:00")
        public String deliverySlot;
    }

    // --- Request DTO for updating delivery details ---
    public static class UpdateDeliveryRequest {
        @Schema(description = "Updated delivery address", example = "456 New Rd, Lyon")
        public String deliveryAddress;

        @Schema(description = "Updated delivery time slot", example = "2025-09-06T10:00-12:00")
        public String deliverySlot;
    }
}
