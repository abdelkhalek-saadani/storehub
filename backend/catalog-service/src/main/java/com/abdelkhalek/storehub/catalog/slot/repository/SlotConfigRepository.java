package com.abdelkhalek.storehub.catalog.slot.repository;

import com.abdelkhalek.storehub.catalog.slot.entity.SlotConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SlotConfigRepository extends JpaRepository<SlotConfig, UUID> {

    List<SlotConfig> findByStoreIdAndActiveTrueAndDayOfWeek(UUID storeId, int dayOfWeek);

    Optional<SlotConfig> findByIdAndStoreId(UUID id, UUID storeId);
}

