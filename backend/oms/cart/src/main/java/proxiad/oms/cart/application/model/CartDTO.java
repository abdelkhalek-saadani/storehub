package proxiad.oms.cart.application.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartDTO {
    private String id;
    private String customerId;
    private List<CartItemDTO> items = new ArrayList<>();
    private MoneyDTO originalTotal;
    private MoneyDTO total;
    private List<String> discounts = new ArrayList<>();
    
    public CartDTO() {}

    public CartDTO(String customerId) {
        this.customerId = customerId;
    }


    @Override
    public String toString() {
        return "CartDTO{\n" +
                "\t id='" + id + "',\n" +
                "\t customerId='" + customerId + "',\n" +
                "\t items=" + items + ",\n"+
                "\t total=" + total +
                '}';
    }

}
