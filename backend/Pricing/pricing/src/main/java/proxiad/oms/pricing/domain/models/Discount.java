package proxiad.oms.pricing.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
// Used to construct concrete discount strategies, it holds data related to the discount rule
public class Discount {

    private String id;
    private String productId;
    private Map<String, String> attributes;

    public String toString() {
        return "the id " + id + " and productId " + productId;
    }

}
