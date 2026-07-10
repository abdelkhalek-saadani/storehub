package com.abdelkhalek.storehub.catalog.pricing.service;


import com.abdelkhalek.storehub.catalog.dtos.PriceItemResponse;
import com.abdelkhalek.storehub.catalog.dtos.PricesRequest;
import com.abdelkhalek.storehub.catalog.dtos.PricesResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;


/**
 * Dummy PricingService for manual testing
 */
@Service
public class FakePricingService implements PricingService {

    private static final BigDecimal DUMMY_UNIT_PRICE = BigDecimal.valueOf(9.99);

    @Override
    public PricesResponse calculateTotal(PricesRequest request) {

        List<PriceItemResponse> items = request.getItems().stream()
                .map(item -> {
                    BigDecimal lineTotal = DUMMY_UNIT_PRICE
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

                    PriceItemResponse response = new PriceItemResponse();
                    response.setProductId(item.getProductId());
                    response.setQuantity(item.getQuantity());
                    response.setUnitPrice(DUMMY_UNIT_PRICE);
                    response.setOriginalLineTotal(lineTotal);
                    response.setFinalLineTotal(lineTotal); // no discount applied in the fake
                    response.setDiscountAmount(BigDecimal.ZERO);
                    response.setAppliedOffer(null);
                    return response;
                })
                .toList();

        BigDecimal originalTotal = items.stream()
                .map(PriceItemResponse::getOriginalLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PricesResponse response = new PricesResponse();
        response.setItems(items);
        response.setOriginalTotal(originalTotal);
        response.setFinalTotal(originalTotal); // no discount
        response.setTotalDiscount(BigDecimal.ZERO);

        return response;
    }
}