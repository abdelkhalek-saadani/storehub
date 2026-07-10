package com.abdelkhalek.storehub.catalog.pricing;

import com.abdelkhalek.storehub.catalog.PricesService;
import com.abdelkhalek.storehub.catalog.dtos.PricesRequest;
import com.abdelkhalek.storehub.catalog.dtos.PricesResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping(("internal"))
public class PricingController {

    private final PricesService pricesService;

    @PostMapping("prices")
    public ResponseEntity<PricesResponse> price(@RequestBody @Valid PricesRequest request) {
        return ResponseEntity.ok(pricesService.getPrices(request));
    }


}