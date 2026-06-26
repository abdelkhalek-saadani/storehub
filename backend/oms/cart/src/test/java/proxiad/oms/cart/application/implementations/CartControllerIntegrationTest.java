package proxiad.oms.cart.application.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import proxiad.oms.cart.application.model.CartDTO;
import proxiad.oms.cart.application.model.CreateCartDTO;
import proxiad.oms.cart.application.model.ProductQuantityDTO;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCart() throws Exception {
        // Given
        CreateCartDTO createCartDTO = new CreateCartDTO();
        String customerId = UUID.randomUUID().toString();
        createCartDTO.setCustomerId(customerId);
        String createCartJson = objectMapper.writeValueAsString(createCartDTO);

        // When
        MvcResult result = mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCartJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andReturn();

        // Then
        CartDTO createdCart = objectMapper.readValue(
                result.getResponse().getContentAsString(), CartDTO.class);
        assertThat(createdCart.getId()).isNotNull();
        assertThat(createdCart.getCustomerId()).isEqualTo(customerId);
            assertThat(createdCart.getItems()).isEmpty();
    }

    @Test
    void shouldNotCreateCart() throws Exception {
        // Given
        CreateCartDTO createCartDTO = new CreateCartDTO();
        String customerId = "InvalidUUID";
        createCartDTO.setCustomerId(customerId);
        String createCartJson = objectMapper.writeValueAsString(createCartDTO);

        // When
        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCartJson))
                .andExpect(status().isBadRequest());


    }


    @Test
    void shouldUpdateProductQuantity() throws Exception {
        // Given - First create a cart
        CreateCartDTO createCartDTO = new CreateCartDTO();
        createCartDTO.setCustomerId(UUID.randomUUID().toString());
        MvcResult createResult = mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCartDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        CartDTO createdCart = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), CartDTO.class);

        // When - Update product quantity
        ProductQuantityDTO productQuantityDTO = new ProductQuantityDTO();
        productQuantityDTO.setCartId(createdCart.getId());
        String productId = UUID.randomUUID().toString();
        productQuantityDTO.setProductId(productId);
        productQuantityDTO.setQuantity(5);

        MvcResult updateResult = mockMvc.perform(put("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productQuantityDTO)))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CartDTO updatedCart = objectMapper.readValue(
                updateResult.getResponse().getContentAsString(), CartDTO.class);
        assertThat(updatedCart.getId()).isEqualTo(createdCart.getId());
        assertThat(updatedCart.getItems()).hasSize(1);
        assertThat(updatedCart.getItems().getFirst().getProductId()).isEqualTo(productId);
        assertThat(updatedCart.getItems().getFirst().getQuantity()).isEqualTo(5);
    }

    @Test
    void shouldGetCart() throws Exception {
        // Given - First create a cart
        CreateCartDTO createCartDTO = new CreateCartDTO();
        createCartDTO.setCustomerId(UUID.randomUUID().toString());
        MvcResult createResult = mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCartDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        CartDTO createdCart = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), CartDTO.class);

        // When - Get the cart
        MvcResult getResult = mockMvc.perform(get("/cart/" + createdCart.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CartDTO retrievedCart = objectMapper.readValue(
                getResult.getResponse().getContentAsString(), CartDTO.class);
        assertThat(retrievedCart.getId()).isEqualTo(createdCart.getId());
        assertThat(retrievedCart.getCustomerId()).isEqualTo(createdCart.getCustomerId());
    }

    @Test
    void shouldResetCart() throws Exception {
        // Given - Create a cart and add a product
        CreateCartDTO createCartDTO = new CreateCartDTO();
        createCartDTO.setCustomerId(UUID.randomUUID().toString());
        MvcResult createResult = mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCartDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        CartDTO createdCart = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), CartDTO.class);

        // Add a product
        ProductQuantityDTO productQuantityDTO = new ProductQuantityDTO();
        productQuantityDTO.setCartId(createdCart.getId());
        productQuantityDTO.setProductId(UUID.randomUUID().toString());
        productQuantityDTO.setQuantity(3);

        mockMvc.perform(put("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productQuantityDTO)))
                .andExpect(status().isOk());

        // When - Reset the cart
        MvcResult resetResult = mockMvc.perform(put("/cart/reset/" + createdCart.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CartDTO resetCart = objectMapper.readValue(
                resetResult.getResponse().getContentAsString(), CartDTO.class);
        assertThat(resetCart.getId()).isEqualTo(createdCart.getId());
        assertThat(resetCart.getItems()).isEmpty();
    }

    @Test
    void shouldDeleteCart() throws Exception {
        // Given - Create a cart
        CreateCartDTO createCartDTO = new CreateCartDTO();
        createCartDTO.setCustomerId(UUID.randomUUID().toString());
        MvcResult createResult = mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCartDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        CartDTO createdCart = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), CartDTO.class);

        // When - Delete the cart
        MvcResult deleteResult = mockMvc.perform(delete("/cart/" + createdCart.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CartDTO deletedCart = objectMapper.readValue(
                deleteResult.getResponse().getContentAsString(), CartDTO.class);
        assertThat(deletedCart.getId()).isEqualTo(createdCart.getId());

        // Verify that the cart is actually deleted by trying to get it
        mockMvc.perform(get("/cart/" + createdCart.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenCreatingCartWithoutCustomerId() throws Exception {
        // Given
        CreateCartDTO createCartDTO = new CreateCartDTO();
        // Not setting customerId
        String createCartJson = objectMapper.writeValueAsString(createCartDTO);

        // When/Then
        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCartJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithInvalidProductQuantity() throws Exception {
        // Given
        CreateCartDTO createCartDTO = new CreateCartDTO();
        createCartDTO.setCustomerId(UUID.randomUUID().toString());
        MvcResult createResult = mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCartDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        String createdCartId = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), CartDTO.class).getId();


        ProductQuantityDTO productQuantityDTO = new ProductQuantityDTO();
        productQuantityDTO.setCartId(createdCartId);
        productQuantityDTO.setProductId(UUID.randomUUID().toString());
        productQuantityDTO.setQuantity(-1);

        // When/Then
        mockMvc.perform(put("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productQuantityDTO)))
                .andExpect(status().isBadRequest());
    }
}