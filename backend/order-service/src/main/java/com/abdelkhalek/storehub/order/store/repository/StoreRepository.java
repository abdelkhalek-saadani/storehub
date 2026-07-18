package com.abdelkhalek.storehub.order.store.repository;

import com.abdelkhalek.storehub.order.store.model.Store;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import java.util.UUID;

public interface StoreRepository extends ReactiveCrudRepository<Store, UUID> {
}
