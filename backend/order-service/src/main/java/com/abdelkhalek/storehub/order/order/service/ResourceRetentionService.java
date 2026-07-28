package com.abdelkhalek.storehub.order.order.service;

import com.abdelkhalek.storehub.order.order.models.Result;
import com.abdelkhalek.storehub.order.order.spi.ProductService;
import com.abdelkhalek.storehub.order.order.spi.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class ResourceRetentionService {

    private final SlotService slotService;
    private final ProductService productService;

    public record RetentionResult(List<UUID> inventoryRetainIds, UUID slotRetainId) {
    }

    public Mono<RetentionResult> retainAll(UUID storeId, UUID cartId, UUID slotId) {
        Mono<Result<List<UUID>>> items = wrap(
                productService.retain(storeId, cartId), "Failed to retain items");
        Mono<Result<UUID>> slot = wrap(
                slotService.retain(storeId, slotId), "Failed to retain slot");

        return Mono.zip(items, slot).flatMap(results -> {
            Result<List<UUID>> itemsResult = results.getT1();
            Result<UUID> slotResult = results.getT2();

            if (itemsResult.isSuccess() && slotResult.isSuccess()) {
                return Mono.just(new RetentionResult(itemsResult.value(), slotResult.value()));
            }
            return releasePartial(itemsResult, slotResult)
                    .then(Mono.error(Exceptions.multiple(collectErrors(itemsResult, slotResult))));
        });
    }

    public Mono<Void> releaseAll(RetentionResult retention) {
        return Mono.when(
                releaseItems(retention.inventoryRetainIds()),
                releaseSlot(retention.slotRetainId())
        );
    }

    private Mono<Void> releasePartial(Result<List<UUID>> itemsResult, Result<UUID> slotResult) {
        List<Mono<Void>> releases = new ArrayList<>();
        if (itemsResult.isSuccess()) releases.add(releaseItems(itemsResult.value()));
        if (slotResult.isSuccess()) releases.add(releaseSlot(slotResult.value()));
        return Mono.when(releases);
    }

    private Mono<Void> releaseSlot(UUID retainId) {
        return slotService.release(retainId)
                .onErrorResume(e -> {
                    log.error("Failed to release delivery slot: {}", retainId, e);
                    return Mono.empty();
                });
    }

    private Mono<Void> releaseItems(List<UUID> retainIds) {
        return productService.release(retainIds);
    }

    private <T> Mono<Result<T>> wrap(Mono<T> operation, String errorMessage) {
        return operation
                .map(Result::success)
                .onErrorResume(e -> {
                    log.error("{}: {}", errorMessage, e.getClass().getSimpleName());
                    return Mono.just(Result.failure(e));
                });
    }

    private List<Throwable> collectErrors(Result<?>... results) {
        return Arrays.stream(results)
                .filter(r -> !r.isSuccess())
                .map(Result::error)
                .toList();
    }
}
