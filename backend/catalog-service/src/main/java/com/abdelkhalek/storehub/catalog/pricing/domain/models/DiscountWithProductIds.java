package com.abdelkhalek.storehub.catalog.pricing.domain.models;

import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.DiscountRule;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 *  Used to construct concrete discount strategies, it holds data related to the discount rule
 */
@Data
@AllArgsConstructor
public class DiscountWithProductIds{
    UUID id;
    UUID storeId;
    DiscountType type;
    DiscountRule rule;
    Instant startsAt;
    Instant endsAt;
    List<UUID> productIds;
}