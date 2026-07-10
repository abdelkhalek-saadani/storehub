package com.abdelkhalek.storehub.catalog.product.dto;


import com.abdelkhalek.storehub.catalog.pricing.domain.models.DiscountType;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.discountrule.DiscountRule;

import java.time.Instant;
import java.util.UUID;

public record DiscountSummary(
        UUID id,
        DiscountType type,
        DiscountRule rule,
        Instant startsAt,
        Instant endsAt
) {}