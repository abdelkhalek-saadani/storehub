package com.abdelkhalek.storehub.order.common.exception;

import com.abdelkhalek.storehub.order.cart.exception.CartItemNotFoundException;
import com.abdelkhalek.storehub.order.cart.exception.CatalogServiceException;
import com.abdelkhalek.storehub.order.cart.exception.ProductNotFoundException;
import com.abdelkhalek.storehub.order.order.exceptions.OrderNotFoundException;
import com.abdelkhalek.storehub.order.order.exceptions.UnauthorizedAccessException;
import com.abdelkhalek.storehub.order.order.exceptions.UnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<?> handleUpstreamError(WebClientResponseException ex) {
        log.error("Handled WebClientResponseException {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode())
                .body(ex.getResponseBodyAsString());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleResponseStatus(ProductNotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage())));
    }

    @ExceptionHandler({CatalogServiceException.class,  UnavailableException.class})
    public Mono<ResponseEntity<Map<String, String>>> handleResponseStatus(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage())));
    }


    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleResponseStatus(ResponseStatusException ex) {
        if (!ex.getStatusCode().equals(HttpStatus.CONFLICT)) {
            log.warn("Handled ResponseStatusException: {}", ex.getReason(), ex);
        }
        log.warn("Handled ResponseStatusException: {}", ex.getReason());
        return Mono.just(ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("message", ex.getReason() != null ? ex.getReason() : "Request failed")));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, String>> handleValidation(WebExchangeBindException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));
        return ResponseEntity.badRequest().body(errors);
    }



    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage())));
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        log.error("Unauthorized access {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage())));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleOrderNotFound(OrderNotFoundException ex) {
        log.error("Order Not found {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message",
                ex.getMessage())));
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleCartItemNotFound(CartItemNotFoundException ex) {
        log.warn("Cart item not found: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage())));
    }
}
