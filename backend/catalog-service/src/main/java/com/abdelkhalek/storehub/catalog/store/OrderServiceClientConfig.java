package com.abdelkhalek.storehub.catalog.store;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;


@Configuration
public class OrderServiceClientConfig {

    @Value("${storehub.order-base-url}")
    private String orderServiceBaseUrl;

    @Bean
    RestClient orderServiceClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        return RestClient.builder()
                .baseUrl(orderServiceBaseUrl)
                .requestInterceptor(bearerTokenInterceptor(authorizedClientManager))
                .build();
    }

    private ClientHttpRequestInterceptor bearerTokenInterceptor(OAuth2AuthorizedClientManager manager) {
        return (request, body, execution) -> {
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId("order-internal-service")
                    .build();

            OAuth2AuthorizedClient authorizedClient = manager.authorize(authorizeRequest);
            String token = authorizedClient.getAccessToken().getTokenValue();

            request.getHeaders().setBearerAuth(token);
            return execution.execute(request, body);
        };
    }

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        var authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        manager.setAuthorizedClientProvider(authorizedClientProvider);
        return manager;
    }
}
