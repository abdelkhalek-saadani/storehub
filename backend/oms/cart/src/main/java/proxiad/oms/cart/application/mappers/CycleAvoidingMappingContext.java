package proxiad.oms.cart.application.mappers;

/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

import java.util.IdentityHashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Context;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;
import proxiad.oms.cart.application.model.CartDTO;
import proxiad.oms.cart.domain.models.Cart;

/**
 * A type to be used as {@link Context} parameter to track cycles in graphs.
 * <p>
 * Depending on the actual use case, the two methods below could also be changed to only accept certain argument types,
 * e.g. base classes of graph nodes, avoiding the need to capture any other objects that wouldn't necessarily result in
 * cycles.
 *
 * @author Andreas Gudian
 */
@Slf4j
public class CycleAvoidingMappingContext {
    private Map<Object, Object> knownInstances = new IdentityHashMap<Object, Object>();
    private Cart storedCart = null;

    @BeforeMapping
    public <T> T getMappedInstance(Object source, @TargetType Class<T> targetType) {
        return (T) knownInstances.get( source );
    }

    @BeforeMapping
    public void storeMappedInstance(Object source, @MappingTarget Object target) {
        // set it here manually, because when mapping from dto to domain
        // the cartItemDto has no info about its cart
        // then in order to set the Cart of cartItem I would get it from the context directly
        if (source instanceof CartDTO){
            storedCart = (Cart) target;
        }
        knownInstances.put( source, target );
    }

    public Cart getStoredCart() {
        return storedCart;
    }

}
