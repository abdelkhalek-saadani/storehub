package com.abdelkhalek.storehub.catalog.product.repository;

import com.abdelkhalek.storehub.catalog.product.entity.SubCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubCategoryRepository extends JpaRepository<SubCategory, UUID> {
    List<SubCategory> findByStoreId(UUID storeId);
    List<SubCategory> findByStoreId(UUID storeId, Pageable pageable);

}