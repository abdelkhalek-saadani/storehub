//package com.proxiad.payment.controller;
//
//import com.proxiad.payment.dto.CreatePaymentRequest;
//import com.proxiad.payment.dto.PaymentResponse;
//import com.proxiad.payment.entity.PaymentEntity;
//import com.proxiad.payment.exception.PaymentNotFoundException;
//import com.proxiad.payment.service.PaymentService;
//import com.proxiad.payment.webhook.WebhookHandler;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.Max;
//import jakarta.validation.constraints.Min;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.data.domain.Sort;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.tags.Tag;
//
//import java.time.LocalDateTime;
//import java.util.Map;
//import java.util.UUID;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/payments/paypal")
//@RequiredArgsConstructor
//@Tag(name = "PayPal Payments", description = "Endpoints for managing PayPal payments")
//public class PayPalControllerSwagger {
//
//    private final PaymentService paymentService;
//    private final WebhookHandler webhookHandler;
//
//    @Operation(summary = "Get payments", description = "Retrieve a list of PayPal payments or filter by captureId, authorizationId, or customerId")
//    @GetMapping
//    public ResponseEntity<?> getPayments(
//            @Parameter(description = "PayPal Capture ID") @RequestParam(required = false) String captureId,
//            @Parameter(description = "Payment status") @RequestParam(required = false) String status,
//            @Parameter(description = "PayPal Authorization ID") @RequestParam(required = false) String authorizationId,
//            @Parameter(description = "Customer ID") @RequestParam(required = false) UUID customerId,
//            @Parameter(description = "Start date filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
//            @Parameter(description = "End date filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
//            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
//            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir,
//            @Parameter(description = "Page number") @RequestParam(defaultValue = "1") @Min(1) int page,
//            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit
//    ) {
//        // ... SAME IMPLEMENTATION AS BEFORE
//        return ResponseEntity.ok("ok");
//    }
//
//    @Operation(summary = "Get payment status", description = "Retrieve the status of a specific payment")
//    @GetMapping("/{paymentId}/status")
//    public ResponseEntity<?> getPaymentStatus(@Parameter(description = "Payment ID") @PathVariable UUID paymentId) {
//        // ... SAME IMPLEMENTATION AS BEFORE
//        return ResponseEntity.ok("ok");
//
//    }
//
//    @Operation(summary = "Get payment by ID", description = "Retrieve a PayPal payment by its ID")
//    @GetMapping("/{paymentId}")
//    public ResponseEntity<?> getPaymentById(@Parameter(description = "Payment ID") @PathVariable UUID paymentId) {
//        // ... SAME IMPLEMENTATION AS BEFORE
//        return ResponseEntity.ok("ok");
//
//    }
//
//    @Operation(summary = "Create PayPal order", description = "Create a new PayPal order")
//    @PostMapping
//    public ResponseEntity<?> createOrder(@Valid @RequestBody CreatePaymentRequest request) {
//        // ... SAME IMPLEMENTATION AS BEFORE
//        return ResponseEntity.ok("");
//
//    }
//
//    @Operation(summary = "Void authorization", description = "Void an authorization for a specific payment")
//    @PostMapping("/{paymentId}/void")
//    public ResponseEntity<?> voidAuthorization(@PathVariable String paymentId) {
//        // ... SAME IMPLEMENTATION AS BEFORE
//        return ResponseEntity.ok("");
//
//    }
//
//    @Operation(summary = "Refund capture", description = "Refund a PayPal capture by payment ID")
//    @PostMapping("/{paymentId}/refund")
//    public ResponseEntity<?> refundCapture(@PathVariable String paymentId) {
//        // ... SAME IMPLEMENTATION AS BEFORE
//        return ResponseEntity.ok("");
//    }
//
//    @Operation(summary = "Capture authorization", description = "Capture a PayPal authorization (testing endpoint)")
//    @PostMapping("/{paymentId}/capture")
//    public ResponseEntity<?> captureAuthorization(@PathVariable String paymentId) {
//        // ... SAME IMPLEMENTATION AS BEFORE
//        return ResponseEntity.ok("");
//
//    }
//
//    @Operation(summary = "Authorize order", description = "Authorize a PayPal order (testing endpoint)")
//    @PostMapping("/{paymentId}/authorize")
//    public ResponseEntity<?> authorizeOrder(@PathVariable String paymentId) {
//        // ... SAME IMPLEMENTATION AS BEFORE
//        return ResponseEntity.ok("");
//
//    }
//
//    @Operation(summary = "Handle PayPal webhook", description = "Receive and process PayPal webhook notifications")
//    @PostMapping("/webhook")
//    public ResponseEntity<Void> handleWebhook(
//            @RequestBody String payload,
//            @RequestHeader("PAYPAL-TRANSMISSION-ID") String transmissionId,
//            @RequestHeader("PAYPAL-CERT-URL") String certUrl,
//            @RequestHeader("PAYPAL-AUTH-ALGO") String authAlgo,
//            @RequestHeader("PAYPAL-TRANSMISSION-SIG") String transmissionSig,
//            @RequestHeader("PAYPAL-TRANSMISSION-TIME") String transmissionTime) {
//        // ... SAME IMPLEMENTATION AS BEFORE
//        return ResponseEntity.internalServerError().build();
//    }
//}
