package com.abdelkhalek.storehub.catalog.inventory.repository;

import com.abdelkhalek.storehub.catalog.inventory.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
}
