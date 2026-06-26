package proxiad.oms.pricing.infrastructure.implementations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import proxiad.oms.pricing.infrastructure.models.DiscountResponse;
import proxiad.oms.pricing.infrastructure.models.UnitPriceResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Slf4j
@Component
public class ExternalProductClient {

    private final WebClient webClient;

    public ExternalProductClient() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:3000")
                .build();
    }

    private Mono<UnitPriceResponse> getUnitPrice(String productId) {

        return webClient.get()
                .uri("/products/{productId}", productId)
                .retrieve()
                .bodyToMono(UnitPriceResponse.class)
                .onErrorResume(e -> {
                    log.info("Error fetching unit price for product: {}" , productId);
                    log.info("Error Message {}",e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<DiscountResponse> getDiscount(String productId) {
        return webClient.get()
                .uri("/discounts?productId={productId}", productId)
                .retrieve()
                .bodyToFlux(DiscountResponse.class)
                .next()
                .doOnNext(discount -> log.info("Received discount: {} for product: {}", discount, productId))
                .onErrorResume(e -> {
                    log.info("Error fetching discount for product: {} with error message: {}" , productId, e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<UnitPriceResponse>> getUnitPrices(List<String> productIds){
        return Flux.fromIterable(productIds)
                .flatMap(this::getUnitPrice)
                .collectList();
    }

    public Mono<List<DiscountResponse>> getDiscounts(List<String> productIds){
        return Flux.fromIterable(productIds)
                .flatMap(this::getDiscount)
                .collectList();
    }


}
