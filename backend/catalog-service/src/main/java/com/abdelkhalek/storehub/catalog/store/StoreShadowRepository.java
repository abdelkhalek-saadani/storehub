package com.abdelkhalek.storehub.catalog.store;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;


public interface StoreShadowRepository extends JpaRepository<StoreShadow, UUID> {

    List<StoreShadow> findAllByStatus(String status);

    StoreShadow findByOwnerId(UUID ownerId);

    Optional<StoreShadow> findBySlug(String slug);

    @Transactional
    @Modifying
    @Query(value = """
        INSERT INTO store_shadow (id,slug, owner_id, status, synced_at)
        VALUES (:id,:slug, :ownerId, :status, :syncedAt)
        ON CONFLICT (id) DO UPDATE
        SET slug= :slug,owner_id = :ownerId, status = :status, synced_at = :syncedAt
        """, nativeQuery = true)
    void upsert(@Param("id") UUID id,@Param("slug") String slug, @Param("ownerId") UUID ownerId,
                @Param("status") String status, @Param("syncedAt") Instant syncedAt);}
