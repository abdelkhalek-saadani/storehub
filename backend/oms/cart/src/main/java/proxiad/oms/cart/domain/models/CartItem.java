package proxiad.oms.cart.domain.models;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.Random;
@Data
public class CartItem {

    private UUID id;
    private UUID productId;

    private int quantity;
    private Money originalUnitPrice;    // à verifier
    private Money unitPrice;            // à verifier
    private Cart cart;                  // à verifier

    public CartItem(UUID productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public static CartItem getRandomCartItem() {
        Random random = new Random();
        CartItem cartItem = new CartItem();
        cartItem.setId(UUID.randomUUID());
        cartItem.setProductId(UUID.randomUUID());
        cartItem.setQuantity(random.nextInt(20));
        Money unitPrice = new Money(BigDecimal.valueOf(random.nextDouble()));
        cartItem.setUnitPrice(unitPrice);
        cartItem.setOriginalUnitPrice(unitPrice);
        return cartItem;
    }

    public CartItem() {}

    // this constructor is for performing deep copy of cartItem plus setting the cart to null
    public CartItem(CartItem cartItem) {
        this.productId = cartItem.getProductId();
        this.quantity = cartItem.getQuantity();
        this.originalUnitPrice = cartItem.getOriginalUnitPrice();
        this.unitPrice = cartItem.getUnitPrice();
        this.cart = null;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof CartItem && productId.equals(((CartItem) obj).productId);
    }






    @Override
    public String toString() {
        return "CartItem{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", cart=" + (cart != null ? cart.getId() : "null") +
                '}';
    }

}
