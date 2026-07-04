package com.abdelkhalek.storehub.catalog.pricing;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    List<ProductEntity> findByStoreIdAndIdIn(UUID storeId, List<UUID> productIds);

    @Query("""
        select distinct p from ProductEntity p
        left join fetch p.discounts d
        where p.storeId = :storeId and p.id in :productIds
        """)
    List<ProductEntity> findByStoreIdAndIdInWithDiscounts(
            @Param("storeId") UUID storeId,
            @Param("productIds") List<UUID> productIds);
}
