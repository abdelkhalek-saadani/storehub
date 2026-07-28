package com.proxiad.payment.controller;

import com.proxiad.payment.dto.CreatePaymentRequest;
import com.proxiad.payment.dto.PaymentResponse;
import com.proxiad.payment.entity.PaymentEntity;
import com.proxiad.payment.exception.PaymentNotFoundException;
import com.proxiad.payment.service.PaymentService;
import com.proxiad.payment.webhook.WebhookHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class PayPalController {

    private final PaymentService paymentService;
    private final WebhookHandler webhookHandler;


    @GetMapping
    public ResponseEntity<?> getPayments(
            @RequestParam(required = false) String captureId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String authorizationId,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit
    ) {

        if (captureId != null && !captureId.isEmpty()) {
            PaymentEntity payment = paymentService.getByCaptureId(captureId);
            if (payment == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Map.of("data", payment));
        }

        if (authorizationId != null && !authorizationId.isEmpty()) {
            PaymentEntity payment = paymentService.getByAuthorizationId(authorizationId);
            if (payment == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Map.of("data", payment));
        }

        if (customerId != null && !customerId.toString().isEmpty()) {
            PaymentEntity payment = paymentService.getByCustomerId(customerId);
            if (payment == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Map.of("data", payment));
        }

        PaymentFilter filter = PaymentFilter.builder()
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page - 1, limit, sort);
        Page<PaymentEntity> paymentsPage = paymentService.getPayments(filter, pageable);

        Map<String, Object> response = Map.of(
                "data", paymentsPage.getContent(),
                "pagination", Map.of(
                        "page", page,
                        "limit", limit,
                        "total", paymentsPage.getTotalElements(),
                        "totalPages", paymentsPage.getTotalPages()
                )
        );

        return ResponseEntity.ok(response);

    }


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

    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable UUID paymentId) {
        PaymentEntity payment = paymentService.getPaymentById(paymentId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("data", payment));

    }

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

    // Testing endpoints - should be removed in production or protected with profiles
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
}