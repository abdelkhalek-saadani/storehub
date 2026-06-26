package proxiad.oms.cart.application.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateCartDTO {

    @NotBlank(message = "Customer ID is required")
    @Pattern(regexp = "^[0-9a-fA-F-]{36}$",
            message = "Invalid UUID format for customerId")
    private String customerId;

    public CreateCartDTO() {}

    public CreateCartDTO(String customerId) {
        this.customerId = customerId;
    }

}
