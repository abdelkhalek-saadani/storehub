package com.abdelkhalek.storehub.order.implementations.product;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.abdelkhalek.storehub.order.infrastructure.implementations.product.ProductClient;
import com.abdelkhalek.storehub.order.infrastructure.models.product.AvailabilityRequest;
import com.abdelkhalek.storehub.order.infrastructure.models.CartItemRequest;
import com.abdelkhalek.storehub.order.infrastructure.models.product.RetainRequest;
import com.abdelkhalek.storehub.order.infrastructure.models.StoreRequest;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "external.product.api.base-url=http://localhost:3333"
})
class ProductClientTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeAll
    static void setUp() {

        wireMockServer = new WireMockServer(
                WireMockConfiguration
                        .options()
                        .port(3333)
                        .extensions(new ResponseTemplateTransformer(true))
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
    void getAvailability_Success() throws Exception {
        // Prepare test data
        List<CartItemRequest> items = Arrays.asList(
                new CartItemRequest("123", 2),
                new CartItemRequest("456", 1)
        );
        StoreRequest store = new StoreRequest("store2");
        AvailabilityRequest request = new AvailabilityRequest(items, store);

        // Configure mock response (here We're using the already existing stub for /check-availability under mappings)
//        stubFor(post(urlEqualTo("/check-availability"))
//                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
//                .withRequestBody(equalToJson(objectMapper.writeValueAsString(request)))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                        .withBody("true")));

        // Execute and verify
        Mono<Boolean> result = productClient.getAvailability(items, store);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        // Verify the request was made
        verify(postRequestedFor(urlEqualTo("/check-availability"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));
    }

    @Test
    void getAvailability_Failure() {
        // Prepare test data
        List<CartItemRequest> items = Arrays.asList(
                new CartItemRequest("123", 2),
                new CartItemRequest("456", 1)
        );
        StoreRequest store = new StoreRequest("STORE-001");

        // Configure mock to return an error
        stubFor(post(urlEqualTo("/check-availability"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        // Execute and verify we get empty result on error
        Mono<Boolean> result = productClient.getAvailability(items, store);

        StepVerifier.create(result)
                .verifyComplete(); // Should complete with no emissions due to onErrorResume
    }

    @Test
    void retain_Success() throws Exception {
        // Prepare test data
        List<CartItemRequest> items = Arrays.asList(
                new CartItemRequest("123", 2),
                new CartItemRequest("456", 1)
        );
        StoreRequest store = new StoreRequest("STORE-001");
        RetainRequest request = new RetainRequest(items, store);

        UUID expectedUuid = UUID.randomUUID();

        String jsonBody = """
                {
                "id": "%s"
                }
                """.formatted(expectedUuid);

        // Configure mock response
        stubFor(post(urlEqualTo("/retain"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(request)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(jsonBody)));

        // Execute
        Mono<UUID> result = productClient.retain(items, store);

        // Verify
        StepVerifier.create(result)
                .expectNext(expectedUuid)
                .verifyComplete();

        // Verify the request was made
        verify(postRequestedFor(urlEqualTo("/retain"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));
    }

    @Test
    void retain_Failure() {
        // Prepare test data
        List<CartItemRequest> items = Arrays.asList(
                new CartItemRequest("123", 2),
                new CartItemRequest("456", 1)
        );
        StoreRequest store = new StoreRequest("STORE-001");

        // Configure mock to return an error
        stubFor(post(urlEqualTo("/retain"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        // Execute and verify we get empty result on error
        Mono<UUID> result = productClient.retain(items, store);

        StepVerifier.create(result)
                .verifyComplete(); // Should complete with no emissions due to onErrorResume
    }


}
