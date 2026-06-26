package proxiad.oms.pricing.infrastructure.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;


@Data
@AllArgsConstructor
public class DiscountResponse {
    private String id;
    private String productId;
    private Map<String, String> attributes;

    public String toString() {
        return "the id " + id + " and productId " + productId;
    }
}
