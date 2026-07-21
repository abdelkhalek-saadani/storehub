package com.abdelkhalek.storehub.order.infrastructure.implementations.slot;

import com.abdelkhalek.storehub.order.order.dto.RetainResponse;
import com.abdelkhalek.storehub.order.infrastructure.models.slot.*;
import com.abdelkhalek.storehub.order.infrastructure.models.StoreRequest;
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




    public SlotClient( @Value("${external.slot.api.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }


    public Mono<Boolean> getAvailability(DeliveryRequest delivery, SlotRequest slot, StoreRequest store) {
        log.info("Checking slot availability..." +
                "slot: {}, store: {}, delivery: {}", slot, store, delivery);


        return webClient.post()
                .uri("/check-availability")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AvailabilityRequest(delivery, slot, store))
                .retrieve()
                .bodyToMono(AvailabilityResponse.class)
                .map(AvailabilityResponse::isAvailable)
                .onErrorResume(e -> {
                    log.info("Error checking availability of slot {} in store :{} with delivery: {} ", slot, store, delivery);
                    log.info("{}", e.getMessage());
                    // try returning Mono.error(e)
                    return Mono.empty();
                });
    }


    public Mono<UUID> retain(DeliveryRequest delivery, SlotRequest slot, StoreRequest store) {
        log.info("Retaining this slot {} ...", slot);
        return webClient.post()
                .uri("retain")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new RetainRequest(delivery, slot, store))
                .retrieve()
                .bodyToMono(RetainResponse.class)
                .map(RetainResponse::getId)
                .onErrorResume(e -> {
                    log.info("Error retaining the slot {}, store {} and delivery {}", slot, store, delivery);
                    log.info("{}", e.getMessage());
                    return Mono.empty();
                });

    }

}
