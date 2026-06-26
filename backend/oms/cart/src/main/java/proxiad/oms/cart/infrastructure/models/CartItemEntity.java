package proxiad.oms.cart.infrastructure.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import proxiad.oms.cart.infrastructure.models.MoneyEntity;

import java.util.UUID;

@Entity(name="customized_cart_item")
@Data
@NoArgsConstructor
public class CartItemEntity {

    // TODO: Change the cartItem id to be a composition of the cartId and productId
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID productId;

    private int quantity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "original_unit_price_value")),
            @AttributeOverride(name = "currency", column = @Column(name = "original_unit_price_currency"))
    })
    private MoneyEntity originalUnitPrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "unit_price_value")),
            @AttributeOverride(name = "currency", column = @Column(name = "unit_price_currency"))
    })
    private MoneyEntity unitPrice;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;  // Changed from Cart to CartEntity

    public CartItemEntity(UUID productId, int quantity) {
        this.id = UUID.randomUUID();
        this.productId = productId;
        this.quantity = quantity;
    }

    public CartItemEntity(UUID productId, int quantity, CartEntity cart) {
        this.id = UUID.randomUUID();
        this.productId = productId;
        this.quantity = quantity;
        this.cart = cart;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CartItemEntity && productId.equals(((CartItemEntity) obj).productId);
    }

    @Override
    public int hashCode() {
        return productId.hashCode();
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