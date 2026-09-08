package com.abdelkhalek.storehub.payment.controller;

import com.abdelkhalek.storehub.payment.dto.CreatePaymentRequest;
import com.abdelkhalek.storehub.payment.dto.PagedResponse;
import com.abdelkhalek.storehub.payment.dto.PaymentResponse;
import com.abdelkhalek.storehub.payment.entity.PaymentEntity;
import com.abdelkhalek.storehub.payment.enums.PaymentStatus;
import com.abdelkhalek.storehub.payment.exception.PaymentNotFoundException;
import com.abdelkhalek.storehub.payment.model.PaymentFilter;
import com.abdelkhalek.storehub.payment.repository.PaymentSpecifications;
import com.abdelkhalek.storehub.payment.service.PaymentService;
import com.abdelkhalek.storehub.payment.webhook.WebhookHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/payments/paypal")
@RequiredArgsConstructor
@Tag(name = "PayPal Payments", description = "PayPal payment lifecycle and webhook handling")
@SecurityRequirement(name = "bearerAuth")
public class PayPalController {

    private final PaymentService paymentService;
    private final WebhookHandler webhookHandler;


    @Operation(summary = "Get the status of a payment")
    @ApiResponse(responseCode = "200", description = "Status found")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    @GetMapping("/{paymentId}/status")
    public ResponseEntity<?> getPaymentStatus(@PathVariable UUID paymentId) {
        PaymentEntity payment = paymentService.getPaymentById(paymentId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("data",
                        Map.of("status", payment.getStatus())
                )
        );
    }

    @Operation(summary = "Get a payment by ID")
    @ApiResponse(responseCode = "200", description = "Payment found")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable UUID paymentId) {
        PaymentEntity payment = paymentService.getPaymentById(paymentId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("data", payment));

    }

    @Operation(summary = "Create a PayPal order for an order",
            description = "This is called by order service at the end of order creation pipeline")
    @PostMapping
    public ResponseEntity<PaymentResponse> createOrder(
            @Valid @RequestBody CreatePaymentRequest request) {
        log.debug("Creating PayPal order for orderId: {}, customerId: {}", request.orderId(),
                request.customerId());

        PaymentResponse response = paymentService.createPayment(
                request.orderId(),
                request.customerId(),
                request.amount()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Void a payment authorization")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    @PostMapping("/{paymentId}/void")
    public ResponseEntity<PaymentResponse> voidAuthorization(@PathVariable UUID paymentId) {
        PaymentEntity payment;
        try {
            payment = paymentService.getById(paymentId);
        } catch (PaymentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        String authorizationId = payment.getAuthorizationId();
        log.info("Voiding authorization: {}", authorizationId);

        PaymentResponse response = paymentService.voidAuthorization(authorizationId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refund a captured payment")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponse> refundCapture(@PathVariable UUID paymentId) {
        PaymentEntity payment;
        try {
            payment = paymentService.getById(paymentId);
        } catch (PaymentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        String captureId = payment.getCaptureId();
        log.info("Refunding capture: {}", captureId);

        PaymentResponse response = paymentService.refundCapture(captureId);
        return ResponseEntity.ok(response);
    }

    // Testing endpoints, should be removed in production or protected with profiles

    @Operation(summary = "Capture an authorized payment",
            description = "Testing endpoint, should be removed or profile-gated in production.")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<PaymentResponse> captureAuthorization(@PathVariable UUID paymentId) {
        PaymentEntity payment;
        try {
            payment = paymentService.getById(paymentId);
        } catch (PaymentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        log.info("Payment: {}", payment);

        String authorizationId = payment.getAuthorizationId();
        log.info("Capturing authorization: {}", authorizationId);

        PaymentResponse response = paymentService.captureAuthorization(authorizationId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Authorize a PayPal order",
            description = "Testing endpoint, should be removed or profile-gated in production.")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    @PostMapping("/{paymentId}/authorize")
    public ResponseEntity<PaymentResponse> authorizeOrder(@PathVariable UUID paymentId) {
        PaymentEntity payment;
        try {
            payment = paymentService.getById(paymentId);
        } catch (PaymentNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        String orderId = payment.getPaymentOrderId();
        log.info("Authorizing order: {}", orderId);

        PaymentResponse response = paymentService.authorizePaypalOrder(orderId);
        return ResponseEntity.ok(response);
    }

    @SecurityRequirements()
    @Operation(summary = "Handle incoming PayPal webhook events")
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("PAYPAL-TRANSMISSION-ID") String transmissionId,
            @RequestHeader("PAYPAL-CERT-URL") String certUrl,
            @RequestHeader("PAYPAL-AUTH-ALGO") String authAlgo,
            @RequestHeader("PAYPAL-TRANSMISSION-SIG") String transmissionSig,
            @RequestHeader("PAYPAL-TRANSMISSION-TIME") String transmissionTime) {

        log.info("Received PayPal webhook with transmission ID: {}", transmissionId);
        log.debug("Received payload: {}", payload);
        webhookHandler.handleWebhook(payload, transmissionId, certUrl, authAlgo, transmissionSig, transmissionTime);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "List payments with filtering and pagination")
    @GetMapping
    public ResponseEntity<PagedResponse<PaymentEntity>> getPayments(
            @RequestParam(required = false) String captureId,
            @RequestParam(required = false) String authorizationId,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Specification<PaymentEntity> spec = PaymentSpecifications.captureIdEquals(captureId)
                .and(PaymentSpecifications.authorizationIdEquals(authorizationId))
                .and(PaymentSpecifications.customerIdEquals(customerId))
                .and(PaymentSpecifications.statusEquals(status))
                .and(PaymentSpecifications.createdBetween(startDate, endDate));

        Page<PaymentEntity> paymentsPage = paymentService.getPayments(spec, pageable);


        return ResponseEntity.ok(PagedResponse.of(paymentsPage));
    }
}