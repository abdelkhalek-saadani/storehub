package proxiad.oms.cart.infrastructure.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import proxiad.oms.cart.infrastructure.models.MoneyEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "customized_cart")
@Data
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID customerId;

    @OneToMany(mappedBy = "cart",
            cascade = CascadeType.ALL, // to save the links automatically with the parent entity save
            orphanRemoval = true,fetch = FetchType.EAGER)
    private List<CartItemEntity> items = new ArrayList<>(); // Initialize to avoid NPE


    @Setter(AccessLevel.NONE)
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "original_total_value")),
            @AttributeOverride(name = "currency", column = @Column(name = "original_total_currency"))
    })
    private MoneyEntity originalTotal;

    @Setter(AccessLevel.NONE)
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "total_value")),
            @AttributeOverride(name = "currency", column = @Column(name = "total_currency"))
    })
    private MoneyEntity total;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cart_discounts", joinColumns = @JoinColumn(name = "cart_id"))
    @Column(name = "discount")
    private List<String> discounts = new ArrayList<>();

    public CartEntity(UUID id) {
        this.id = id;
        this.originalTotal = new MoneyEntity(BigDecimal.ZERO);
        this.total = new MoneyEntity(BigDecimal.ZERO);
    }

    public CartEntity(UUID id, UUID customerId, MoneyEntity total) {
        this.id = id;
        this.customerId = customerId;
        this.total = total;
        this.originalTotal = new MoneyEntity(total.getValue());
    }

    public CartEntity() {
        this.originalTotal = new MoneyEntity(BigDecimal.ZERO);
        this.total = new MoneyEntity(BigDecimal.ZERO);
        this.discounts = new ArrayList<>();
    }

    public void setOriginalTotal(MoneyEntity total) {
        if ((total!=null)&&total.getValue().compareTo(BigDecimal.ZERO) > 0) this.originalTotal = new MoneyEntity(total.getValue());
        else this.originalTotal = new MoneyEntity(BigDecimal.ZERO);
    }

    public void setTotal(MoneyEntity total) {
        if ((total!=null)&&total.getValue().compareTo(BigDecimal.ZERO) > 0) this.total = new MoneyEntity(total.getValue());
        else this.total = new MoneyEntity(BigDecimal.ZERO);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", items=" + items +
                ", total=" + total +
                '}';
    }
}