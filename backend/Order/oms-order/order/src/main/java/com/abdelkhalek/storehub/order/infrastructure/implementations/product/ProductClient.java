package com.abdelkhalek.storehub.order.infrastructure.implementations.product;


import com.abdelkhalek.storehub.order.infrastructure.models.CartItemRequest;
import com.abdelkhalek.storehub.order.infrastructure.models.product.AvailabilityRequest;
import com.abdelkhalek.storehub.order.infrastructure.models.product.AvailabilityResponse;
import com.abdelkhalek.storehub.order.infrastructure.models.product.RetainRequest;
import com.abdelkhalek.storehub.order.infrastructure.models.product.RetainResponse;
import com.abdelkhalek.storehub.order.infrastructure.models.product.*;
import com.abdelkhalek.storehub.order.infrastructure.models.StoreRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ProductClient {

    private final WebClient webClient;




    public ProductClient( @Value("${external.product.api.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<Boolean> getAvailability(List<CartItemRequest> items, StoreRequest store) {
        log.info("Checking items availability..." +
                "items: {}, store: {}", items, store);


        return webClient.post()
                .uri("/check-availability")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AvailabilityRequest(items, store))
                .retrieve()
                .bodyToMono(AvailabilityResponse.class)
                .map(AvailabilityResponse::isAvailable)
                .onErrorResume(e -> {
                    log.info("Error checking availability of {} in store {}", items, store);
                    log.info("{}", e.getMessage());
                    return Mono.empty();
                });
    }


    public Mono<UUID> retain(List<CartItemRequest> items, StoreRequest store) {
        log.info("Retaining these items {} from this store {}...", items, store);
        return webClient.post()
                .uri("retain")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RetainRequest(items, store))
                .retrieve()
                .bodyToMono(RetainResponse.class)
                .map(RetainResponse::getId)
                .onErrorResume(e -> {
                    log.info("Error retaining of {} in store {}", items, store);
                    log.info("{}", e.getMessage());
                    return Mono.empty();
                });

    }



}
