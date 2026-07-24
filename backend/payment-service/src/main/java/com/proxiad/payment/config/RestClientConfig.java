package com.proxiad.payment.config;

import com.proxiad.payment.exception.PayPalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableConfigurationProperties({PaypalProperties.class})
@RequiredArgsConstructor
@Slf4j
public class RestClientConfig {

    private final PaypalProperties props;

    @Bean
    public RestClient restClient(
            OAuth2AuthorizedClientManager authorizedClientManager
    ) {
        OAuth2ClientHttpRequestInterceptor bearerTokenInterceptor =
                new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        bearerTokenInterceptor.setClientRegistrationIdResolver(request -> "paypal");
        return RestClient
                .builder()
                .requestInterceptor(bearerTokenInterceptor)
                .baseUrl(props.baseUrl())
                .defaultStatusHandler(HttpStatusCode::isError, this::handleErrorResponse)
                .build();
    }


    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        return new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
    }

    private void handleErrorResponse(HttpRequest request, ClientHttpResponse response) throws IOException {
        String errorBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        log.error("PayPal API error - Status: {}, Body: {}", response.getStatusCode(), errorBody);
        throw new PayPalApiException("PayPal API error: " + response.getStatusCode() + " - " + errorBody);
    }
}
