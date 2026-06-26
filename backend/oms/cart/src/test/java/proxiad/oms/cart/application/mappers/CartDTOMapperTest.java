package proxiad.oms.cart.application.mappers;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import proxiad.oms.cart.application.model.CartDTO;
import proxiad.oms.cart.application.model.CartItemDTO;
import proxiad.oms.cart.domain.models.Cart;
import proxiad.oms.cart.domain.models.CartItem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class CartDTOMapperTest {

    private CartDTOMapper mapper;

    @Mock
    private CycleAvoidingMappingContext context;

    @Mock
    private Cart mockCart;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CartDTOMapper.class);

        // Setup context mock for cart item mapper test (because when we test the cart item mapper individually there is no cart context provided)
        // Used lenient() here because there is a minority of tests that doesn't use the stubbed context
        lenient().when(context.getStoredCart()).thenReturn(mockCart);
    }

    @Test
    void toCart_ShouldMapCartDTOToCart() {
        // Arrange
        CartItemDTO itemDTO1 = new CartItemDTO();
        String item1Id = UUID.randomUUID().toString();
        itemDTO1.setId(item1Id);
        itemDTO1.setQuantity(2);
        String product1Id = UUID.randomUUID().toString();
        itemDTO1.setProductId(product1Id);

        CartItemDTO itemDTO2 = new CartItemDTO();
        String item2Id = UUID.randomUUID().toString();
        itemDTO2.setId(item2Id);
        itemDTO2.setQuantity(1);
        String product2Id = UUID.randomUUID().toString();
        itemDTO2.setProductId(product2Id);

        List<CartItemDTO> itemDTOs = Arrays.asList(itemDTO1, itemDTO2);

        CartDTO cartDTO = new CartDTO();
        String cartDTOId = UUID.randomUUID().toString();
        cartDTO.setId(cartDTOId);
        String cartCustomerId = UUID.randomUUID().toString();
        cartDTO.setCustomerId(cartCustomerId);
        cartDTO.setItems(itemDTOs);

        // Act
        CycleAvoidingMappingContext context = spy(new CycleAvoidingMappingContext());
        Cart result = mapper.toCart(cartDTO, context);


        // Assert
        assertNotNull(result);
        assertEquals(cartDTOId, result.getId().toString());
        assertEquals(cartCustomerId, result.getCustomerId().toString());
        assertNotNull(result.getItems());
        assertEquals(2, result.getItems().size());

        // Verify items were mapped correctly
        CartItem mappedItem1 = result.getItems().get(0);
        assertEquals(item1Id, mappedItem1.getId().toString());
        assertEquals(2, mappedItem1.getQuantity());
        assertEquals(product1Id, mappedItem1.getProductId().toString());
        assertEquals(result, mappedItem1.getCart());

        CartItem mappedItem2 = result.getItems().get(1);
        assertEquals(item2Id, mappedItem2.getId().toString());
        assertEquals(1, mappedItem2.getQuantity());
        assertEquals(product2Id, mappedItem2.getProductId().toString());
        assertEquals(result, mappedItem2.getCart());

        // Verify context was used
        verify(context, times(2)).getStoredCart();
    }

        @Test
        void fromCart_ShouldMapCartToCartDTO() {
            // Arrange
            Cart cart = new Cart();
            UUID cartId = UUID.randomUUID();
            cart.setId(cartId);
            UUID customerId = UUID.randomUUID();
            cart.setCustomerId(customerId);

            CartItem item1 = new CartItem();
            UUID item1Id = UUID.randomUUID();
            item1.setId(item1Id);
            item1.setQuantity(2);
            UUID product1Id = UUID.randomUUID();
            item1.setProductId(product1Id);
            item1.setCart(cart);

            CartItem item2 = new CartItem();
            UUID item2Id = UUID.randomUUID();
            item2.setId(item2Id);
            item2.setQuantity(1);
            UUID product2Id = UUID.randomUUID();
            item2.setProductId(product2Id);
            item2.setCart(cart);

            List<CartItem> items = Arrays.asList(item1, item2);

            cart.setItems(items);

            // Act
            CartDTO result = mapper.fromCart(cart);

            // Assert
            assertNotNull(result);
            assertEquals(cartId.toString(), result.getId());
            assertEquals(customerId.toString(), result.getCustomerId());
            assertNotNull(result.getItems());
            assertEquals(2, result.getItems().size());

            // Verify items were mapped correctly
            CartItemDTO mappedItemDTO1 = result.getItems().get(0);
            assertEquals(item1Id.toString(), mappedItemDTO1.getId());
            assertEquals(2, mappedItemDTO1.getQuantity());
            assertEquals(product1Id.toString(), mappedItemDTO1.getProductId());

            CartItemDTO mappedItemDTO2 = result.getItems().get(1);
            assertEquals(item2Id.toString(), mappedItemDTO2.getId());
            assertEquals(1, mappedItemDTO2.getQuantity());
            assertEquals(product2Id.toString(), mappedItemDTO2.getProductId());
        }

    @Test
    void toCartItem_ShouldMapCartItemDTOToCartItem() {
        // Arrange
        CartItemDTO itemDTO = new CartItemDTO();
        String itemId = UUID.randomUUID().toString();
        itemDTO.setId(itemId);
        itemDTO.setQuantity(3);
        String productId = UUID.randomUUID().toString();
        itemDTO.setProductId(productId);

        // Act
        CartItem result = mapper.toCartItem(itemDTO, context);

        // Assert
        assertNotNull(result);
        assertEquals(itemId, result.getId().toString());
        assertEquals(3, result.getQuantity());
        assertEquals(productId, result.getProductId().toString());
        assertEquals(mockCart, result.getCart());

        // Verify context was used
        verify(context).getStoredCart();
    }

    @Test
    void fromCartItem_ShouldMapCartItemToCartItemDTO() {
        // Arrange
        CartItem item = new CartItem();
        UUID itemId = UUID.randomUUID();
        item.setId(itemId);
        item.setQuantity(3);
        UUID productId = UUID.randomUUID();
        item.setProductId(productId);
        item.setCart(mockCart);

        // Act
        CartItemDTO result = mapper.fromCartItem(item); // changing second argument won't affecct anything

        // Assert
        assertNotNull(result);
        assertEquals(itemId.toString(), result.getId());
        assertEquals(3, result.getQuantity());
        assertEquals(productId.toString(), result.getProductId());
    }

    @Test
    void toCartItemList_ShouldMapListOfDTOsToCartItems() {
        // Arrange
        CartItemDTO itemDTO1 = new CartItemDTO();
        String item1Id = UUID.randomUUID().toString();
        itemDTO1.setId(item1Id);
        String product1Id = UUID.randomUUID().toString();
        itemDTO1.setProductId(product1Id);
        itemDTO1.setQuantity(2);

        CartItemDTO itemDTO2 = new CartItemDTO();
        String item2Id = UUID.randomUUID().toString();
        itemDTO2.setId(item2Id);
        String product2Id = UUID.randomUUID().toString();
        itemDTO2.setProductId(product2Id);
        itemDTO2.setQuantity(3);

        List<CartItemDTO> dtos = Arrays.asList(itemDTO1, itemDTO2);

        // Act
        List<CartItem> result = mapper.toCartItemList(dtos, context);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify each item has the cart set from context
        for(CartItem item : result) {
            assertEquals(mockCart, item.getCart());
        }

        assertEquals(item1Id, result.getFirst().getId().toString());
        assertEquals(2, result.getFirst().getQuantity());
        assertEquals(product1Id, result.getFirst().getProductId().toString());
        assertEquals(item2Id, result.get(1).getId().toString());
        assertEquals(3, result.get(1).getQuantity());
        assertEquals(product2Id, result.get(1).getProductId().toString());


        // Verify context was used for each item
        verify(context, times(2)).getStoredCart();
    }

    @Test
    void toCartItemDTOList_ShouldMapListOfCartItemsToDTOs() {
        // Arrange
        CartItem item1 = new CartItem();
        UUID item1Id = UUID.randomUUID();
        item1.setId(item1Id);
        item1.setQuantity(2);
        item1.setCart(mockCart);

        CartItem item2 = new CartItem();
        UUID item2Id = UUID.randomUUID();
        item2.setId(item2Id);
        item2.setQuantity(3);
        item2.setCart(mockCart);

        List<CartItem> items = Arrays.asList(item1, item2);

        // Act
        List<CartItemDTO> result = mapper.toCartItemDTOList(items);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(item1Id.toString(), result.get(0).getId());
        assertEquals(item2Id.toString(), result.get(1).getId());
    }

    @Test
    void toCart_WithEmptyItemsList_ShouldReturnCartWithEmptyItems() {
        // Arrange
        CartDTO cartDTO = new CartDTO();
        String cartId = UUID.randomUUID().toString();
        cartDTO.setId(cartId);
        String customerId = UUID.randomUUID().toString();
        cartDTO.setCustomerId(customerId);
        cartDTO.setItems(Collections.emptyList());

        // Act
        Cart result = mapper.toCart(cartDTO, context);

        // Assert
        assertNotNull(result);
        assertEquals(cartId, result.getId().toString());
        assertEquals(customerId, result.getCustomerId().toString());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void fromCart_WithEmptyItemsList_ShouldReturnDTOWithEmptyItems() {
        // Arrange
        Cart cart = new Cart();
        UUID cartId = UUID.randomUUID();
        cart.setId(cartId);
        UUID customerId = UUID.randomUUID();
        cart.setCustomerId(customerId);
        cart.setItems(Collections.emptyList());

        // Act
        CartDTO result = mapper.fromCart(cart);

        // Assert
        assertNotNull(result);
        assertEquals(cartId.toString(), result.getId());
        assertEquals(customerId.toString(), result.getCustomerId());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void toCart_WithNullItems_ShouldHandleNullGracefully() {
        // Arrange
        CartDTO cartDTO = new CartDTO();
        String cartId = UUID.randomUUID().toString();
        cartDTO.setId(cartId);
        String customerId = UUID.randomUUID().toString();
        cartDTO.setCustomerId(customerId);
        cartDTO.setItems(null);

        // Act
        Cart result = mapper.toCart(cartDTO, context);

         // Assert

        assertNull(cartDTO.getItems());
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void addCartFromContext_ShouldSetCartFromContext() {
        // Arrange
        CartItem cartItem = new CartItem();

        // Act - this method is called automatically by MapStruct during mapping
        mapper.addCartFromContext(cartItem, context);

        // Assert
        assertEquals(mockCart, cartItem.getCart());
        verify(context).getStoredCart();
    }
}
