package com.abdelkhalek.storehub.catalog.product.repository;

import com.abdelkhalek.storehub.catalog.product.entity.SaleEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SaleEventRepository extends JpaRepository<SaleEvent, UUID> {
    boolean existsByStoreIdAndSlug(UUID storeId, String slug);

    List<SaleEvent> findByStoreId(UUID storeId, Pageable pageable);
}
