package proxiad.oms.cart.infrastructure.implementations;


import org.springframework.data.repository.CrudRepository;
import proxiad.oms.cart.infrastructure.models.CartEntity;

import java.util.UUID;

public interface CartJpaRepository extends CrudRepository<CartEntity, UUID>  {
}
