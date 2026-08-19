package com.abdelkhalek.storehub.payment;


import com.abdelkhalek.storehub.payment.common.config.StorehubProperties;
import com.abdelkhalek.storehub.payment.entity.PaymentEntity;
import com.abdelkhalek.storehub.payment.entity.ProcessedWebhookEventEntity;
import com.abdelkhalek.storehub.payment.enums.PaymentStatus;
import com.abdelkhalek.storehub.payment.event.PaymentStatusUpdateEvent;
import com.abdelkhalek.storehub.payment.repository.PaymentRepository;
import com.abdelkhalek.storehub.payment.repository.ProcessedWebhookEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;

import java.time.Duration;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 23456)
@Testcontainers
@EnableConfigurationProperties(StorehubProperties.class)
class WebhookIT {

    static final int WIREMOCK_PORT = 23456;

    @Autowired
    StorehubProperties props;

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
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    ProcessedWebhookEventRepository processedWebhookEventRepository;
    @MockitoSpyBean
    EventPublisher spyEventPublisher;

    UUID paymentId;
    String paypalOrderId = "PAYPAL-ORDER-ID";
    PaymentEntity payment;

    @Autowired
    private AmqpAdmin rabbitAdmin;

    private static final String TEST_QUEUE = "test.payment.status.queue";

    @BeforeEach
    void setUpTestBinding() {
        Queue queue = new Queue(TEST_QUEUE, false, false, false); // auto-delete
        Binding binding = BindingBuilder
                .bind(queue)
                .to(new TopicExchange(props.rabbit().exchange()))
                .with("payment.status.updated");

        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);
    }

    @BeforeEach
    void seedPayment() {
        paymentRepository.deleteAll();
        payment = paymentRepository.save(PaymentEntity.builder()
                .orderId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .paymentOrderId(paypalOrderId)
                .status(PaymentStatus.CREATED)
                .build());
        paymentId = payment.getId();
    }

    @Test
    void orderApproved_updatesStatus_authorizesOrder_andPublishesEvent() throws Exception {
        stubVerifySignature("SUCCESS");
        stubAuthorizeOrder();

        String payload = orderApprovedPayload("evt-1", paypalOrderId);

        mockMvc.perform(post("/api/payments/paypal/webhook")
                        .header("PAYPAL-TRANSMISSION-ID", "tx-1")
                        .header("PAYPAL-CERT-URL", "https://api.paypal.com/cert")
                        .header("PAYPAL-AUTH-ALGO", "SHA256withRSA")
                        .header("PAYPAL-TRANSMISSION-SIG", "sig")
                        .header("PAYPAL-TRANSMISSION-TIME", "2026-07-01T00:00:00Z")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            PaymentEntity updated = paymentRepository.findById(paymentId).orElseThrow();
            assertThat(updated.getStatus().name()).isEqualTo(PaymentStatus.APPROVED.name());

            PaymentStatusUpdateEvent msg = (PaymentStatusUpdateEvent) rabbitTemplate
                    .receiveAndConvert(TEST_QUEUE, 5000);
            assertThat(msg).isNotNull();
            assertThat(msg.orderId()).isEqualTo(updated.getOrderId());
            assertThat(msg.newStatus().name()).isEqualTo(updated.getStatus().name());
            assertThat(msg.paymentId()).isEqualTo(updated.getId());
        });

        verify(postRequestedFor(urlEqualTo("/v2/checkout/orders/" + paypalOrderId + "/authorize")));
    }

    @Test
    void duplicateEvent_isSkipped() throws Exception {
        stubVerifySignature("SUCCESS");
        stubAuthorizeOrder();

        String payload = orderApprovedPayload("evt-dup", paypalOrderId);

        // first delivery
        mockMvc.perform(post("/api/payments/paypal/webhook")
                        .header("PAYPAL-TRANSMISSION-ID", "tx-1")
                        .header("PAYPAL-CERT-URL", "https://api.paypal.com/cert")
                        .header("PAYPAL-AUTH-ALGO", "SHA256withRSA")
                        .header("PAYPAL-TRANSMISSION-SIG", "sig")
                        .header("PAYPAL-TRANSMISSION-TIME", "2025-01-01T00:00:00Z")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                assertThat(processedWebhookEventRepository.findAll()).hasSize(1));

        // duplicate redelivery (PayPal retries same event id)
        mockMvc.perform(post("/api/payments/paypal/webhook")
                        .header("PAYPAL-TRANSMISSION-ID", "tx-2")
                        .header("PAYPAL-CERT-URL", "https://api.paypal.com/cert")
                        .header("PAYPAL-AUTH-ALGO", "SHA256withRSA")
                        .header("PAYPAL-TRANSMISSION-SIG", "sig")
                        .header("PAYPAL-TRANSMISSION-TIME", "2025-01-01T00:00:01Z")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        // still only one processed record, authorize called only once
        assertThat(processedWebhookEventRepository.findAll()).hasSize(1);
        verify(1, postRequestedFor(urlEqualTo("/v2/checkout/orders/" + paypalOrderId + "/authorize")));
        org.mockito.Mockito.
                verify(spyEventPublisher, Mockito.times(1))
                .publish(Mockito.any(PaymentStatusUpdateEvent.class));
    }

    @Test
    void verificationFailure_doesNotUpdatePayment() throws Exception {
        stubVerifySignature("FAILURE");

        String payload = orderApprovedPayload("evt-bad-sig", paypalOrderId);

        mockMvc.perform(post("/api/payments/paypal/webhook")
                        .header("PAYPAL-TRANSMISSION-ID", "tx-1")
                        .header("PAYPAL-CERT-URL", "https://api.paypal.com/cert")
                        .header("PAYPAL-AUTH-ALGO", "SHA256withRSA")
                        .header("PAYPAL-TRANSMISSION-SIG", "bad-sig")
                        .header("PAYPAL-TRANSMISSION-TIME", "2025-01-01T00:00:00Z")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().is4xxClientError());

        PaymentEntity unchanged = paymentRepository.findById(paymentId).orElseThrow();
        assertThat(unchanged.getStatus()).isEqualTo(PaymentStatus.CREATED);
        org.mockito.Mockito.
                verify(spyEventPublisher, Mockito.times(0))
                .publish(Mockito.any(PaymentStatusUpdateEvent.class));
    }

    private void stubVerifySignature(String status) {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.post(urlEqualTo("/v1/notifications/verify-webhook-signature"))
                .willReturn(okJson("{\"verification_status\":\"" + status + "\"}")));
    }

    private void stubAuthorizeOrder() {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.post(urlEqualTo("/v2/checkout/orders/" + paypalOrderId + "/authorize"))
                .willReturn(okJson("""
                        { "id": "RESPONSE-ID-1",
                          "status": "CREATED",
                          "purchase_units":
                            [
                              {"payments": {
                                "authorizations":
                                [{"id":"AUTH-ID-1" ,"status": "CREATED"}]}
                              }
                            ]
                        }
                        """)));
    }

    private String orderApprovedPayload(String eventId, String orderId) {
        return """
                {
                  "id": "%s",
                  "event_type": "CHECKOUT.ORDER.APPROVED",
                  "resource_type": "checkout-order",
                  "resource": { "id": "%s" }
                }
                """.formatted(eventId, orderId);
    }
}