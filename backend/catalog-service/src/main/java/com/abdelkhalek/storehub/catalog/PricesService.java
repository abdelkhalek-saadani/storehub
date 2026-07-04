package com.abdelkhalek.storehub.catalog;

import com.abdelkhalek.storehub.catalog.dtos.PricesRequest;
import com.abdelkhalek.storehub.catalog.dtos.PricesResponse;
import org.springframework.stereotype.Service;

@Service
public class PricesService {

    public PricesResponse getPrices(PricesRequest request) {
        // Step 2: check products availability (there is sufficient qty or not, this will look for
        // the product lots)
        // Step 3: lookup the products and get the relevant discount from db
        // Step 4: uses the pricing package to perform the totals calculation, maybe with minor
        // editing to make it more clean
        // Step 5: map the result to the response dto
        // Finally: return the result

        return new PricesResponse();
    }

}
