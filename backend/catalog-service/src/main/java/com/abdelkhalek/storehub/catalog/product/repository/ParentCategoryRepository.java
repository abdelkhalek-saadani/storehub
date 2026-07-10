package com.abdelkhalek.storehub.catalog.product.repository;

import com.abdelkhalek.storehub.catalog.product.entity.ParentCategory;
import com.abdelkhalek.storehub.catalog.product.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParentCategoryRepository extends JpaRepository<ParentCategory, UUID> {
    List<ParentCategory> findByStoreId(UUID storeId);
}