package com.abdelkhalek.storehub.payment;

import com.abdelkhalek.storehub.payment.dto.CreatePaymentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureWireMock(port = 23456)
class PayPalControllerIT {
    static final int WIREMOCK_PORT = 23456;

    @Autowired
    ObjectMapper objectMapper;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management-alpine");


    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("paypal.base-url", () -> "http://localhost:" + WIREMOCK_PORT);
    }


    @Autowired
    MockMvc mockMvc;

    @Test
    void createOrder_success() throws Exception {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.post(urlPathEqualTo("/v2/checkout" +
                        "/orders"))
                .willReturn(okJson("""
                        {
                          "id": "PAYPAL-ORDER-ID",
                          "status": "CREATED",
                          "links": [{"rel":"approve","href":"https://paypal.com/approve/xyz"}]
                        }
                        """)));

        var request = new CreatePaymentRequest(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("49.99"));

        mockMvc.perform(post("/api/payments/paypal")
                        .with(jwt()
                                .authorities(new SimpleGrantedAuthority("ROLE_SERVICE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.approvalUrl").value("https://paypal.com/approve/xyz"));
    }
}
