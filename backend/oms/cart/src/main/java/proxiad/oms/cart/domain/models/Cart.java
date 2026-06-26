package proxiad.oms.cart.domain.models;


import lombok.Data;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
public class Cart {


    private UUID id;
    private UUID customerId;

    // TODO: apply defencive copy for the setter and getter of items
    private List<CartItem> items = new ArrayList<>();
    private Money originalTotal;
    private Money total;
    private List<String> discounts = new ArrayList<>(); // à verifier



    public Cart(UUID customerId) {
        this();
        this.customerId = customerId;
    }

    public Cart(UUID id, UUID customerId, Money total) {
        this.id = id;
        this.customerId = customerId;
        this.total = total;
    }

    public Cart(){
        this.originalTotal = new Money(BigDecimal.ZERO);
        this.total = new Money(BigDecimal.ZERO);
        this.discounts = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Cart{\n" +
                "\t id='" + id + "',\n" +
                "\t customerId='" + customerId + "',\n" +
                "\t items=" + items + ",\n"+
                "\t total=" + total +
                '}';
    }

}
