package com.abdelkhalek.storehub.order.order.service.slot;

import com.abdelkhalek.storehub.order.order.dto.AvailabilityResponse;
import com.abdelkhalek.storehub.order.order.dto.slot.RetainRequest;
import com.abdelkhalek.storehub.order.order.dto.slot.RetainResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
public class SlotClient {

    private final WebClient webClient;
    private final WebClient catalogWebClient;


    public SlotClient(@Value("${storehub.catalog-base-url}") String baseUrl, WebClient catalogWebClient) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.catalogWebClient = catalogWebClient;
    }


    public Mono<Boolean> getAvailability(UUID storeId, UUID slotId) {

        return webClient.get()
                .uri(ub -> ub
                        .path("/api/delivery-slots/check-availability")
                        .queryParam("storeId", storeId)
                        .queryParam("slotId", slotId).build())
                .retrieve()
                .bodyToMono(AvailabilityResponse.class)
                .map(AvailabilityResponse::isAvailable)
                .doOnSubscribe(s -> log.info("Checking slot availability... slot: {}, store: {}", slotId, storeId))
                .onErrorResume(e -> {
                    log.warn("Error checking availability of slot {} in store :{}", slotId, storeId);
                    log.debug("{}", e.getMessage());
                    return Mono.empty();
                });
    }


    public Mono<UUID> retain(UUID storeId, UUID slotId) {
        log.info("Retaining this slot {} ...", slotId);
        return catalogWebClient.post()
                .uri(ub -> ub
                        .path("/api/delivery-slots/reserve")
                        .queryParam("storeId", storeId).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RetainRequest(slotId))
                .retrieve()
                .bodyToMono(RetainResponse.class)
                .map(RetainResponse::retainId)
                .onErrorResume(e -> {
                    log.warn("Error retaining the slot {}, store {}", slotId, storeId);
                    log.debug("{}", e.getMessage());
                    return Mono.empty();
                });

    }

}
