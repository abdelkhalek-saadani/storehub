package proxiad.oms.pricing.domain.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.proxiad.events.CartChangedEvent;
import com.proxiad.events.CartItemEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Cart {
    private String cartId;
    private List<Item> items = new ArrayList<>();
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    public String toString() {
        calculateTotalDiscount();
        return "The total discount "+ totalDiscount+",\nitems = " + items + "and the total discount = " + totalDiscount + "\n" +
                "   The original total is " + getOriginalTotal() + " and total after discount is " + getFinalTotal();
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public BigDecimal getOriginalTotal() {
        return items.stream()
                .map(Item::getOriginalSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getFinalTotal() {
        return items.stream()
                .map(Item::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void calculateTotalDiscount() {
        this.totalDiscount = getOriginalTotal().subtract(getFinalTotal());
    }
}
