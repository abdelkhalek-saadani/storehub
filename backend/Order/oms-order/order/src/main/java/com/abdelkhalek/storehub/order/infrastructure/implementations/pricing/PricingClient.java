package com.abdelkhalek.storehub.order.infrastructure.implementations.pricing;

import com.abdelkhalek.storehub.order.infrastructure.models.pricing.PriceRequest;
import com.abdelkhalek.storehub.order.infrastructure.models.pricing.PriceResponse;
import com.abdelkhalek.storehub.order.infrastructure.models.slot.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class PricingClient {

    private final WebClient webClient;




    public PricingClient(@Value("${external.pricing.api.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }



    public Mono<PriceResponse> calculateOrderTotals(PriceRequest priceRequest) {

        log.info("Calculating order totals..." +
                "\n price request: {}", priceRequest);


        return webClient.post()
                .uri("/calculate-totals")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(priceRequest)
                .retrieve()
                .bodyToMono(PriceResponse.class)
                .doOnNext(price -> {
                    log.info("Calculated order total value: {}", price.getTotal());
                })
                .onErrorResume(e -> {
                    log.info("Error getting totals of {}", priceRequest);
                    log.info("{}", e.getMessage());
//                      return Mono.error(e);
                return Mono.empty();
                });

    }
}
