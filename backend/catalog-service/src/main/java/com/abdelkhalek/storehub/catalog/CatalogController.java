package com.abdelkhalek.storehub.catalog;

import com.abdelkhalek.storehub.catalog.dtos.PricesRequest;
import com.abdelkhalek.storehub.catalog.dtos.PricesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController("/internal")
public class CatalogController {

    private final PricesService pricesService;

    @PostMapping("prices")
    public ResponseEntity<PricesResponse> price(@RequestBody PricesRequest request) {
        return ResponseEntity.ok(pricesService.getPrices(request));
    }


}
