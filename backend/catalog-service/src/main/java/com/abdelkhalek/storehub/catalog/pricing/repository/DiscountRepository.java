package com.abdelkhalek.storehub.catalog.pricing.repository;

import com.abdelkhalek.storehub.catalog.pricing.entity.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface DiscountRepository extends JpaRepository<DiscountEntity, UUID> {

    @Query("""
        select distinct d from DiscountEntity d
        join fetch d.products p
        where d.storeId = :storeId
          and p.id in :productIds
          and d.startsAt <= :now
          and d.endsAt > :now
        """)
    List<DiscountEntity> findActiveDiscountsForProducts(
            @Param("storeId") UUID storeId,
            @Param("productIds") List<UUID> productIds,
            @Param("now") Instant now);

    @Query("""
    select count(d) > 0 from DiscountEntity d
    join d.products p
    where d.storeId = :storeId
      and p.id = :productId
      and d.startsAt < :endsAt
      and d.endsAt > :startsAt
    """)
    boolean existsOverlappingForProduct(@Param("storeId") UUID storeId,
                                        @Param("productId") UUID productId,
                                        @Param("startsAt") Instant startsAt,
                                        @Param("endsAt") Instant endsAt);

}

