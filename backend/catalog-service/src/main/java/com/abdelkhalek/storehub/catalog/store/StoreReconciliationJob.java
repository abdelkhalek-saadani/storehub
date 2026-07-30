package com.abdelkhalek.storehub.catalog.store;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoreReconciliationJob {

    private final StoreShadowRepository storeShadowRepository;
    private final RestClient orderServiceClient;

    @Scheduled(cron = "0 0 2 * * *") // 2 AM daily
    public void reconcile() {
        try {
            List<StoreSummary> stores = orderServiceClient.get()
                    .uri("/api/stores")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<StoreSummary>>() {
                    });

            Instant now = Instant.now();

            if (stores != null && !stores.isEmpty()) {
                stores.forEach(s -> storeShadowRepository.upsert(s.id(),s.slug(), s.ownerId(),
                        s.status()
                        , now));
                log.info("Reconciliation completed: {} stores synced", stores.size());
            } else {
                log.warn("No stores found, nothing to reconcile. Check if there are any stores in" +
                        " order service");
            }
        } catch (Exception e) {
            log.error("Store reconciliation failed", e);
        }
    }
}

record StoreSummary(UUID id, String slug, UUID ownerId, String status) {
}
