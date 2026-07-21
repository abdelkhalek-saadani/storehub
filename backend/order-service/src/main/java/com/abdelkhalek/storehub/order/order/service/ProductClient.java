package com.abdelkhalek.storehub.order.order.service;


import com.abdelkhalek.storehub.order.order.dto.AvailabilityRequest;
import com.abdelkhalek.storehub.order.order.dto.AvailabilityResponse;
import com.abdelkhalek.storehub.order.order.dto.RetainRequest;
import com.abdelkhalek.storehub.order.order.dto.RetainResponse;
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
    private final WebClient catalogWebClient;

    public ProductClient(@Value("${storehub.catalog-base-url}") String baseUrl, WebClient catalogWebClient) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.catalogWebClient = catalogWebClient;
    }

    public Mono<Boolean> getAvailability(AvailabilityRequest request) {
        log.info("Checking items availability..." +
                "storeId: {}, items: {}", request.storeId(), request.items());


        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("api/inventory/check-availability")
                        .queryParam("storeId", request.storeId()).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request.items())
                .retrieve()
                .bodyToMono(AvailabilityResponse.class)
                .map(AvailabilityResponse::isAvailable)
                .onErrorResume(e -> {
                    log.warn("Error checking availability of {} in store {}", request.items(),
                            request.storeId());
                    log.debug("{}", e.getMessage());
                    return Mono.empty();
                });
    }


    public Mono<List<UUID>> retain(RetainRequest request) {
        log.info("Retaining these items {} from this store {}...", request.items(), request.storeId());
        return catalogWebClient.post()
                .uri(ub -> ub
                        .queryParam("storeId", request.storeId())
                        .path("api/inventory/reservations").build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RetainRequest(request.items(), request.storeId()))
                .retrieve()
                .bodyToMono(RetainResponse.class)
                .map(RetainResponse::getRetainIds)
                .onErrorResume(e -> {
                    log.warn("Error retaining of {} in store {}", request.items(),
                            request.storeId());
                    log.debug("{}", e.getMessage());
                    return Mono.empty();
                });

    }


}
