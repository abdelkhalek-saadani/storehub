package proxiad.oms.pricing.infrastructure.models;

import lombok.Data;

@Data
public class UnitPriceResponse {
    private String id;
    private String name;
    private String unitPrice;
}
