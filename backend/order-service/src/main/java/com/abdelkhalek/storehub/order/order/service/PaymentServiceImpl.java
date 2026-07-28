package com.abdelkhalek.storehub.order.order.service;

import com.abdelkhalek.storehub.order.order.dto.PaymentRequest;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.dto.PaymentResponse;
import com.abdelkhalek.storehub.order.order.spi.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final WebClient paymentWebClient;

    public PaymentServiceImpl(WebClient paymentWebClient) {
        this.paymentWebClient = paymentWebClient;
    }

    @Override
    public Mono<PaymentResponse> getPaymentApprovalLink(Order order) {
        PaymentRequest paymentRequest = new PaymentRequest(order.getId(), order.getUserId(),
                order.getFinalTotal());
        return paymentWebClient.post()
                .uri("/api/payments/paypal")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(paymentRequest)
                .retrieve()
                .bodyToMono(PaymentResponse.class)
                .onErrorResume(e -> {
                    log.warn("Error getting payment with request {}", paymentRequest);
                    log.debug("{}", e.getMessage());
                    return Mono.empty();
                });
    }
}
