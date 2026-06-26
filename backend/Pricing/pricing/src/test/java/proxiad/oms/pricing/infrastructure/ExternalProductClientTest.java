package proxiad.oms.pricing.infrastructure;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import proxiad.oms.pricing.ProductWireMockServer;
import proxiad.oms.pricing.infrastructure.implementations.ExternalProductClient;
import proxiad.oms.pricing.infrastructure.models.DiscountResponse;
import proxiad.oms.pricing.infrastructure.models.UnitPriceResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;


@SpringBootTest
@ActiveProfiles("test")
public class ExternalProductClientTest {

    @Autowired
    private ExternalProductClient externalProductClient;
    private ProductWireMockServer wireMockServer;

    private List<String> productIds;

    @BeforeEach
    public void setup() {

        wireMockServer = new ProductWireMockServer(3000);
        wireMockServer.start();

        productIds = Arrays.asList(
                "53af7942-0b98-4b0e-a585-3274aca7614a",
                "63d363df-2db5-430d-a142-0dfe600283d6",
                "bca25930-ffd4-44c0-b730-6dd487c4ef2c"
        );
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void getUnitPrices_WithValidProductIds_ReturnsExpectedPrices() {
        // Arrange - Test specific data is already set up in @BeforeEach

        // Act
        Mono<List<UnitPriceResponse>> result = externalProductClient.getUnitPrices(productIds);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(responses -> {
                    // Verify expected size
                    if (responses.size() != 3) {
                        return false;
                    }

                    // Verify individual product prices
                    boolean hasFirstProduct = responses.stream()
                            .anyMatch(p -> p.getId().equals("53af7942-0b98-4b0e-a585-3274aca7614a")
                                    && p.getUnitPrice().equals("200"));

                    boolean hasSecondProduct = responses.stream()
                            .anyMatch(p -> p.getId().equals("63d363df-2db5-430d-a142-0dfe600283d6")
                                    && p.getUnitPrice().equals("100"));

                    boolean hasThirdProduct = responses.stream()
                            .anyMatch(p -> p.getId().equals("bca25930-ffd4-44c0-b730-6dd487c4ef2c")
                                    && p.getUnitPrice().equals("100"));

                    return hasFirstProduct && hasSecondProduct && hasThirdProduct;
                })
                .verifyComplete();
    }

    @Test
    public void getDiscounts_WithValidProductIds_ReturnsDiscountsForAllProducts() {
        // Arrange - Test specific data is already set up in @BeforeEach

        // Act
        Mono<List<DiscountResponse>> result = externalProductClient.getDiscounts(productIds);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(responses -> {
                    // Verify expected size
                    return responses.size() == 3;

                })
                .verifyComplete();
    }
}