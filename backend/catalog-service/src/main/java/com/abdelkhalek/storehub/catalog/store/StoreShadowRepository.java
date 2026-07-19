package com.abdelkhalek.storehub.catalog.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;


public interface StoreShadowRepository extends JpaRepository<StoreShadow, UUID> {

    List<StoreShadow> findAllByStatus(String status);

    StoreShadow findByOwnerId(UUID ownerId);

    @Transactional
    @Modifying
    @Query(value = """
        INSERT INTO store_shadow (id, owner_id, status, synced_at)
        VALUES (:id, :ownerId, :status, :syncedAt)
        ON CONFLICT (id) DO UPDATE
        SET owner_id = :ownerId, status = :status, synced_at = :syncedAt
        """, nativeQuery = true)
    void upsert(@Param("id") UUID id, @Param("ownerId") UUID ownerId,
                @Param("status") String status, @Param("syncedAt") Instant syncedAt);}
