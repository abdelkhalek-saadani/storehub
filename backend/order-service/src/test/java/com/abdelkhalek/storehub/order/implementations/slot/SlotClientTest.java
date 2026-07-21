package com.abdelkhalek.storehub.order.implementations.slot;



/*@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "external.slot.api.base-url=http://localhost:4444"
})
class SlotClientTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private SlotClient slotClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {

        wireMockServer = new WireMockServer(
                WireMockConfiguration
                        .options()
                        .port(4444)
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

        AvailabilityRequest request = new AvailabilityRequest(
                new DeliveryRequest(),
                new SlotRequest(),
                new StoreRequest("store-id")
        );

        String jsonResponse = """
                {
                "available": true
                }
                """;

        // Configure mock response
        stubFor(post(urlEqualTo("/check-availability"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(request)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(jsonResponse)));

        // Execute and verify
        Mono<Boolean> result = slotClient.getAvailability(request.getDelivery(), request.getSlot(), request.getStore());

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

        AvailabilityRequest request = new AvailabilityRequest(
                new DeliveryRequest(),
                new SlotRequest(),
                new StoreRequest("store-id")
        );

        // Configure mock to return an error
        stubFor(post(urlEqualTo("/check-availability"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        // Execute and verify we get empty result on error
        Mono<Boolean> result = slotClient.getAvailability(request.getDelivery(), request.getSlot(), request.getStore());

        StepVerifier.create(result)
                .verifyComplete(); // Should complete with no emissions due to onErrorResume
    }

    @Test
    void retain_Success() throws Exception {
        // Prepare test data
        AvailabilityRequest request = new AvailabilityRequest(
                new DeliveryRequest(),
                new SlotRequest(),
                new StoreRequest("store-id")
        );

        UUID expectedId = UUID.randomUUID();
        String jsonResponse = """
                {
                "id": "%s"
                }
                """.formatted(expectedId.toString());


        // Configure mock response
        stubFor(post(urlEqualTo("/retain"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson(objectMapper.writeValueAsString(request)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(jsonResponse)));

        // Execute
        Mono<UUID> result = slotClient.retain(request.getDelivery(), request.getSlot(), request.getStore());

        // Verify
        StepVerifier.create(result)
                .expectNext(expectedId)
                .verifyComplete();

        // Verify the request was made
        verify(postRequestedFor(urlEqualTo("/retain"))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE)));
    }

    @Test
    void retain_Failure() {
        // Prepare test data
        AvailabilityRequest request = new AvailabilityRequest(
                new DeliveryRequest(),
                new SlotRequest(),
                new StoreRequest("store-id")
        );

        // Configure mock to return an error
        stubFor(post(urlEqualTo("/retain"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        // Execute and verify we get empty result on error
        Mono<UUID> result = slotClient.retain(request.getDelivery(), request.getSlot(), request.getStore());

        StepVerifier.create(result)
                .verifyComplete(); // Should complete with no emissions due to onErrorResume
    }


}*/
