package com.abdelkhalek.storehub.catalog.pricing;

import com.abdelkhalek.storehub.catalog.dtos.AppliedOffer;
import com.abdelkhalek.storehub.catalog.dtos.PriceItemResponse;
import com.abdelkhalek.storehub.catalog.pricing.domain.models.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceItemMapper {

    @Mapping(target = "unitPrice", source = "originalUnitPrice")
    @Mapping(target = "originalLineTotal", source = ".", qualifiedByName = "originalLineTotal")
    @Mapping(target = "finalLineTotal", source = ".", qualifiedByName = "finalLineTotal")
    @Mapping(target = "discountAmount", source = ".", qualifiedByName = "discountAmount")
    @Mapping(target = "appliedOffer", source = ".", qualifiedByName = "appliedOffer")
    PriceItemResponse toResponse(Item item);

    List<PriceItemResponse> toResponses(List<Item> items);

    @Named("originalLineTotal")
    default BigDecimal originalLineTotal(Item item) {
        return item.getOriginalUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    @Named("finalLineTotal")
    default BigDecimal finalLineTotal(Item item) {
        return item.getFinalUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    @Named("discountAmount")
    default BigDecimal discountAmount(Item item) {
        return finalLineTotal(item).subtract(originalLineTotal(item));
    }

    // TODO: Add applied offer id and type
    @Named("appliedOffer")
    default AppliedOffer appliedOffer(Item item) {
        return item.getAppliedDiscounts().stream().findAny()
                .map(d -> new AppliedOffer(null, d.getDescription(), null))
                .orElse(null);
    }
}