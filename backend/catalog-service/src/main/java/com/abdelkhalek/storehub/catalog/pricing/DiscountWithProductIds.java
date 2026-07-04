package com.abdelkhalek.storehub.catalog.pricing;

import lombok.Data;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class DiscountWithProductIds{
    UUID id;
    UUID storeId;
    DiscountType type;
    DiscountRule rule;
    Instant startsAt;
    Instant endsAt;
    List<UUID> productIds;
}