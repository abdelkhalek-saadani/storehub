package com.abdelkhalek.storehub.catalog.inventory.repository;

import com.abdelkhalek.storehub.catalog.inventory.entity.StockEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, UUID> {

    Optional<StockEntity> findByStoreIdAndProductId(UUID storeId, UUID productId);

    // Row-level pessimistic lock, kept available for hot SKUs if optimistic
    // retries start contending too much in practice. Not the default path.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StockEntity s where s.storeId = :storeId and s.productId = :productId")
    Optional<StockEntity> findByStoreIdAndProductIdForUpdate(UUID storeId, UUID productId);

    List<StockEntity> findByStoreIdAndProductIdIn(UUID storeId, List<UUID> productIds);
}
