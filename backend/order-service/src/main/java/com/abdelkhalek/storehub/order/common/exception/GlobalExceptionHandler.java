package com.abdelkhalek.storehub.order.common.exception;

import com.abdelkhalek.storehub.order.cart.exception.CartItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, String>> handleValidation(WebExchangeBindException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));
        return ResponseEntity.badRequest().body(errors);
    }


    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleResponseStatus(ResponseStatusException ex) {
        log.warn("Handled ResponseStatusException: {}", ex.getReason(), ex);
        return Mono.just(ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("message", ex.getReason() != null ? ex.getReason() : "Request failed")));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage())));
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleCartItemNotFound(CartItemNotFoundException ex) {
        log.warn("Cart item not found: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage())));
    }
}
