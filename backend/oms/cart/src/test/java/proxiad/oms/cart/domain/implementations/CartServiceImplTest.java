package proxiad.oms.cart.domain.implementations;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import proxiad.oms.cart.domain.models.*;
import proxiad.oms.cart.domain.spi.CartRepository;
import proxiad.oms.cart.domain.spi.EventPublisher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private CartServiceImpl cartService;



    private UUID cartId;
    private Cart cart;
    private CartItem cartItem;
    private UUID productId;

    @BeforeEach
    void setUp() {
        cartId = UUID.randomUUID();
        productId = UUID.randomUUID();

        cart = new Cart();
        cart.setId(cartId);
        cart.setTotal(new Money());
        cart.setItems(new ArrayList<>());

        cartItem = new CartItem(productId, 1);
    }

    @Test
    @DisplayName("Should set cart total successfully")
    void setTotal_ShouldUpdateCartTotal() {
        // Arrange
        Money newTotal = new Money();
        newTotal.setValue(BigDecimal.valueOf(100.0));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        Cart result = cartService.setTotal(cartId, newTotal);

        // Assert
        assertEquals(newTotal, result.getTotal());
        verify(cartRepository).findById(cartId);
        verify(cartRepository).save(cart); // verify(cartRepository).save(any(Cart.class)); This approach is more flexible, in case my service creates a new object, modifies it and then saves it
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when setting total for non-existent cart")
    void setTotal_ShouldThrowException_WhenCartNotFound() {
        // Arrange
        Money newTotal = new Money();
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> cartService.setTotal(cartId, newTotal)
        );

        assertEquals("Cart not found with ID: " + cartId, exception.getMessage());
        verify(cartRepository).findById(cartId);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should get cart successfully")
    void getCart_ShouldReturnCart() {
        // Arrange
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        // Act
        Cart result = cartService.getCart(cartId);

        // Assert
        assertNotNull(result);
        assertEquals(cartId, result.getId());
        verify(cartRepository).findById(cartId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when getting non-existent cart")
    void getCart_ShouldThrowException_WhenCartNotFound() {
        // Arrange
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> cartService.getCart(cartId)
        );

        assertEquals("Cart not found with ID: " + cartId, exception.getMessage());
        verify(cartRepository).findById(cartId);
    }

    @Test
    @DisplayName("Should create cart successfully")
    void create_ShouldCreateAndReturnCart() {
        // Arrange
        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        Cart result = cartService.create(cart);

        // Assert
        assertNotNull(result);
        assertEquals(cart, result);
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("Should reset cart successfully")
    void reset_ShouldClearItemsAndResetTotal() {
        // Arrange
        cart.getItems().add(cartItem);
        Money total = new Money(BigDecimal.valueOf(50.0));
        cart.setTotal(total);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        Cart result = cartService.reset(cartId);

        // Assert
        assertTrue(result.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, result.getTotal().getValue());
        verify(cartRepository).findById(cartId);
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("Should delete cart successfully")
    void delete_ShouldDeleteAndReturnCart() {
        // Arrange
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.deleteById(cartId)).thenReturn(cart);

        // Act
        Cart result = cartService.delete(cartId);

        // Assert
        assertNotNull(result);
        assertEquals(cart, result);
        verify(cartRepository).findById(cartId);
        verify(cartRepository).deleteById(cartId);
    }

    @Test
    @DisplayName("Should add new item to cart when setQuantity with new product")
    void setQuantity_ShouldAddNewItem_WhenProductNotInCartAndQuantityPositive() {
        // Arrange
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        Cart result = cartService.setQuantity(cartId, productId, 2);

        // Assert
        assertEquals(1, result.getItems().size());
        assertEquals(productId, result.getItems().getFirst().getProductId());
        assertEquals(2, result.getItems().getFirst().getQuantity());

        // Verify event published
        ArgumentCaptor<ItemAddedEvent> eventCaptor = ArgumentCaptor.forClass(ItemAddedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        ItemAddedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(cartId, capturedEvent.getCartId());
        assertEquals(1, capturedEvent.getCartItems().size());
        assertEquals(productId, capturedEvent.getCartItems().getFirst().getProductId());
        assertEquals(2, capturedEvent.getCartItems().getFirst().getQuantity());
    }

    @Test
    @DisplayName("Should remove item from cart when setQuantity with zero quantity")
    void setQuantity_ShouldRemoveItem_WhenQuantityZeroOrLess() {
        // Arrange
        cartItem.setCart(cart);
        cart.getItems().add(cartItem);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        Cart result = cartService.setQuantity(cartId, productId, 0);

        // Assert
        assertTrue(result.getItems().isEmpty());

        // Verify event published
        ArgumentCaptor<ItemRemovedEvent> eventCaptor = ArgumentCaptor.forClass(ItemRemovedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        ItemRemovedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(cartId, capturedEvent.getCartId());
        assertTrue(capturedEvent.getCartItems().isEmpty());
    }

    @Test
    @DisplayName("Should update item quantity when setQuantity with existing product")
    void setQuantity_ShouldUpdateQuantity_WhenProductExistsInCart() {
        // Arrange
        cartItem.setCart(cart);
        cart.getItems().add(cartItem);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        Cart result = cartService.setQuantity(cartId, productId, 5);

        // Assert
        assertEquals(1, result.getItems().size());
        assertEquals(5, result.getItems().getFirst().getQuantity());

        // Verify event published
        ArgumentCaptor<ItemQuantityChangedEvent> eventCaptor = ArgumentCaptor.forClass(ItemQuantityChangedEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        ItemQuantityChangedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(cartId, capturedEvent.getCartId());
        assertEquals(1, capturedEvent.getCartItems().size());
        assertEquals(productId, capturedEvent.getCartItems().getFirst().getProductId());
        assertEquals(5, capturedEvent.getCartItems().getFirst().getQuantity());
    }
}