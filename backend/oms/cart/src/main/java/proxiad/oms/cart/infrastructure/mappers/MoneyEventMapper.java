package proxiad.oms.cart.infrastructure.mappers;

import org.mapstruct.Mapper;
import proxiad.oms.cart.domain.models.Money;
import proxiad.oms.cart.infrastructure.models.MoneyEvent;

@Mapper(componentModel = "spring")
public interface MoneyEventMapper {

    MoneyEvent toMoneyEvent(Money money);

    Money fromMoneyEvent(MoneyEvent moneyEvent);
}
