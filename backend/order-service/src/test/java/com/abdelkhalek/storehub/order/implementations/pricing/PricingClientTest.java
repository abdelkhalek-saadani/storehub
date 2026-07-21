/*
package com.abdelkhalek.storehub.order.implementations.pricing;




import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.abdelkhalek.storehub.order.order.models.Money;
import com.abdelkhalek.storehub.order.infrastructure.implementations.pricing.PricingClient;
import com.abdelkhalek.storehub.order.order.dto.CartItemRequest;
import com.abdelkhalek.storehub.order.infrastructure.models.pricing.CartItemResponse;
import com.abdelkhalek.storehub.order.infrastructure.models.pricing.PriceRequest;
import com.abdelkhalek.storehub.order.infrastructure.models.pricing.PriceResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "external.pricing.api.base-url=http://localhost:5555"
})
class PricingClientTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private PricingClient pricingClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {

        wireMockServer = new WireMockServer(
                WireMockConfiguration
                        .options()
                        .port(5555)
        );
        wireMockServer.start();

        // Configure WireMock
        configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void tearDown() {
        WireMock.reset();
    }

    @Test
    void calculateOrderTotals_Success() throws Exception {
        // Prepare test data

        CartItemRequest cartItemRequest = new CartItemRequest();
        cartItemRequest.setProductId("some-id");
        cartItemRequest.setQuantity(1);

        CartItemRequest cartItemRequest1 = new CartItemRequest();
        cartItemRequest1.setProductId("some-id1");
        cartItemRequest1.setQuantity(10);

        PriceRequest priceRequest = new PriceRequest();
        priceRequest.setItems(List.of(cartItemRequest, cartItemRequest1));

        PriceResponse expectedResponse = new PriceResponse();
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setQuantity(cartItemRequest.getQuantity());
        cartItemResponse.setProductId(cartItemRequest.getProductId());
        cartItemResponse.setSubtotal(new Money(BigDecimal.valueOf(500)));
        cartItemResponse.setUnitPrice(new Money(BigDecimal.valueOf(500)));
        cartItemResponse.setOriginalUnitPrice(new Money(BigDecimal.valueOf(600)));
        CartItemResponse cartItemResponse1 = new CartItemResponse();
        cartItemResponse1.setQuantity(cartItemRequest1.getQuantity());
        cartItemResponse1.setProductId(cartItemRequest1.getProductId());
        cartItemResponse1.setSubtotal(new Money(BigDecimal.valueOf(444)));
        cartItemResponse1.setUnitPrice(new Money(BigDecimal.valueOf(44.4)));
        cartItemResponse1.setOriginalUnitPrice(new Money(BigDecimal.valueOf(44.4)));
        Money expectedMoney = new Money(BigDecimal.valueOf(999));
        List<CartItemResponse> cartItemResponses = Arrays.asList(cartItemResponse, cartItemResponse1);
        expectedResponse.setItems(cartItemResponses);
        expectedResponse.setTotal(expectedMoney);



        String jsonResponse = """
                {
                "items": [
                    {
                        "productId": "%s",
                        "quantity": "%d",
                        "subtotal": {"value":"%s","currency": "EUR"},
                        "unitPrice": {"value":"%s","currency": "EUR"},
                        "originalUnitPrice": {"value":"%s","currency": "EUR"}
                    },
                    {
                        "productId": "%s",
                        "quantity": "%d",
                        "subtotal": {"value":"%s","currency": "EUR"},
                        "unitPrice": {"value":"%s","currency": "EUR"},
                        "originalUnitPrice": {"value":"%s","currency": "EUR"}
                    }
                ],
                "total":{
                    "value": "%s",
                    "currency": "EUR"
                }
                }
                """.formatted(
                        cartItemResponse.getProductId(), cartItemResponse.getQuantity(), cartItemResponse.getSubtotal().getValue().toString(), cartItemResponse.getUnitPrice().getValue().toString(), cartItemResponse.getOriginalUnitPrice().getValue().toString(),
                cartItemResponse1.getProductId(), cartItemResponse1.getQuantity(), cartItemResponse1.getSubtotal().getValue().toString(), cartItemResponse1.getUnitPrice().getValue().toString(), cartItemResponse1.getOriginalUnitPrice().getValue().toString(),
                expectedResponse.getTotal().getValue().toString()
        );

        // Configure mock response
        stubFor(post(urlEqualTo("/calculate-totals"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(priceRequest)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(jsonResponse)));

        // Execute and verify
        Mono<PriceResponse> result = pricingClient.calculateOrderTotals(priceRequest);

        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    if (response.getItems().size() != 2) return false;

                    CartItemResponse item1 = response.getItems().get(0);
                    CartItemResponse item2 = response.getItems().get(1);

                    boolean itemsMatch = item1.getProductId().equals(cartItemResponse.getProductId()) &&
                            item1.getQuantity() == cartItemResponse.getQuantity() &&
                            item1.getSubtotal().getValue().equals(cartItemResponse.getSubtotal().getValue()) &&
                            item1.getUnitPrice().getValue().equals(cartItemResponse.getUnitPrice().getValue()) &&
                            item1.getOriginalUnitPrice().getValue().equals(cartItemResponse.getOriginalUnitPrice().getValue()) &&

                            item2.getProductId().equals(cartItemResponse1.getProductId()) &&
                            item2.getQuantity() == cartItemResponse1.getQuantity() &&
                            item2.getSubtotal().getValue().equals(cartItemResponse1.getSubtotal().getValue()) &&
                            item2.getUnitPrice().getValue().equals(cartItemResponse1.getUnitPrice().getValue()) &&
                            item2.getOriginalUnitPrice().getValue().equals(cartItemResponse1.getOriginalUnitPrice().getValue());

                    Money total = response.getTotal();
                    boolean totalMatch = total.getValue().equals(expectedMoney.getValue()) &&
                            total.getCurrency().equals(expectedMoney.getCurrency());

                    return itemsMatch && totalMatch;
                })
                .verifyComplete();

        // Verify the request was made
        verify(postRequestedFor(urlEqualTo("/calculate-totals"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));
    }

    @Test
    void calculateOrderTotals_Failure() {
        // Prepare test data

        PriceRequest priceRequest = new PriceRequest();

        // Configure mock to return an error
        stubFor(post(urlEqualTo("/calculate-totals"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        // Execute and verify we get empty result on error
        Mono<PriceResponse> result = pricingClient.calculateOrderTotals(priceRequest);

        StepVerifier.create(result)
                .verifyComplete(); // Should complete with no emissions due to onErrorResume
    }


}

*/
