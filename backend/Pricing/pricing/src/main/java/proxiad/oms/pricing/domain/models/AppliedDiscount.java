package proxiad.oms.pricing.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppliedDiscount {
    // Later, I may use this field to return details about the discounts applied along with the total (event)
    private String description;
    private BigDecimal amountPerUnit;

    public String toString() {
        return "description = " + description + ", this discount substracts " + amountPerUnit + " from each product's   unit price";
    }

}
