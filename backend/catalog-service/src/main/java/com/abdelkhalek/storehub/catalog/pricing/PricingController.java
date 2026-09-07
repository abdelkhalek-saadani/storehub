package com.abdelkhalek.storehub.catalog.pricing;

import com.abdelkhalek.storehub.catalog.pricing.dto.PricesRequest;
import com.abdelkhalek.storehub.catalog.pricing.dto.PricesResponse;
import com.abdelkhalek.storehub.catalog.pricing.service.PricesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping(("internal"))
@Tag(name = "Pricing", description = "Inter-service pricing calculations")
public class PricingController {

    private final PricesService pricesService;

    @Operation(summary = "Calculate prices for the given request")
    @ApiResponse(responseCode = "200", description = "Prices calculated")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @PostMapping("prices")
    public ResponseEntity<PricesResponse> price(@RequestBody @Valid PricesRequest request) {
        return ResponseEntity.ok(pricesService.getPrices(request));
    }


}