package com.abdelkhalek.storehub.order.cart.services.price;

import com.abdelkhalek.storehub.order.cart.exception.CatalogServiceException;
import com.abdelkhalek.storehub.order.cart.exception.CatalogServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricesService {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

    private final WebClient catalogWebClient;


    public Mono<PricesResponse> fetchPrices(PricesRequest request) {
        return catalogWebClient.post()
                .uri("internal/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        HttpStatus.BAD_REQUEST::equals,
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new CatalogServiceException("Invalid price request: " + body)
                                ))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> Mono.error(
                                new CatalogServiceUnavailableException("Catalog service returned a server error")
                        )
                )
                .bodyToMono(PricesResponse.class)
                .timeout(
                        REQUEST_TIMEOUT,
                        Mono.error(new CatalogServiceUnavailableException(
                                "Catalog service timed out after " + REQUEST_TIMEOUT.getSeconds() + "s"
                        ))
                )
                .onErrorMap(
                        WebClientRequestException.class,
                        ex -> new CatalogServiceUnavailableException(
                                "Could not reach catalog-service: " + ex.getMessage()
                        )
                )
                .doOnError(error -> log.error(
                        "Failed to fetch prices from catalog-service: {}", error.getMessage()
                ));
    }
}