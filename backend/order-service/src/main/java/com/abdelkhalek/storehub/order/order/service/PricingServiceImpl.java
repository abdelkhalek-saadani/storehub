package com.abdelkhalek.storehub.order.order.service;

import com.abdelkhalek.storehub.order.shared.dto.PriceItemResponse;
import com.abdelkhalek.storehub.order.shared.dto.PricesRequest;
import com.abdelkhalek.storehub.order.shared.dto.PricesResponse;
import com.abdelkhalek.storehub.order.shared.service.PricesService;
import com.abdelkhalek.storehub.order.order.mapper.OrderMapper;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.models.OrderItem;
import com.abdelkhalek.storehub.order.order.spi.PricingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class PricingServiceImpl implements PricingService {
    @Autowired
    private PricesService pricesService;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * Populate order items with their prices and their final totals
     * with discounts applied
     * It request the items unit prices and discounts from the catalog-service
     *
     * @param order order with items (productId, and quantity)
     * @return the passed order with prices populated and discounts applied
     */
    @Override
    public Mono<Order> calculateOrderTotals(Order order) {
        PricesRequest pricesRequest = orderMapper.toPricesRequest(order);
        Mono<PricesResponse> pricesResponseMono = pricesService.fetchPrices(pricesRequest);

        return pricesResponseMono.map((pricesResponse -> {
            log.debug("Prices Response: {}", pricesResponse);
            for (PriceItemResponse pr : pricesResponse.items()) {
                log.debug("Price Item: {}", pr);
            }
            List<OrderItem> items =
                    orderMapper.fromPriceItemsResponse(pricesResponse.items());

            order.setItems(items);
            order.setFinalTotal(pricesResponse.finalTotal());
            order.setTotalDiscount(pricesResponse.totalDiscount());
            order.setOriginalTotal(pricesResponse.originalTotal());
            log.debug("order after prices populated: {}", order);
            return order;
        }));
    }
}
