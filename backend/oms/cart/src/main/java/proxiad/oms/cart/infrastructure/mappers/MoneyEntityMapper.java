package proxiad.oms.cart.infrastructure.mappers;

import org.mapstruct.Mapper;
import proxiad.oms.cart.domain.models.Money;
import proxiad.oms.cart.infrastructure.models.MoneyEntity;

@Mapper(componentModel = "spring")
public interface MoneyEntityMapper {

    MoneyEntity toMoneyEntity(Money money);
    Money fromMoneyEntity(MoneyEntity moneyEntity);

}
