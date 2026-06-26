package proxiad.oms.cart.application.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class ProductQuantityDTO {
    @NotBlank(message = "It is required")
    @Pattern(regexp = "^[0-9a-fA-F-]{36}$",
            message = "Invalid UUID format")
    private String cartId;

    @NotBlank(message = "Customer ID is required")
    @Pattern(regexp = "^[0-9a-fA-F-]{36}$",
            message = "Invalid UUID format for customerId")
    private String productId;

    @Min(value =0)
    private int quantity;
}
