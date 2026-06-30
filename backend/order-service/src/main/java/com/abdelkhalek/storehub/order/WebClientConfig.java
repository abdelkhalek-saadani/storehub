package com.abdelkhalek.storehub.order;

import com.abdelkhalek.storehub.order.security.StorehubProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(StorehubProperties.class)
public class WebClientConfig {

    @Bean
    public WebClient keycloakWebClient(
            StorehubProperties props,
            ReactiveClientRegistrationRepository clientRegistrations,
            ReactiveOAuth2AuthorizedClientService authorizedClientService) {

        var authorizedClientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                        clientRegistrations, authorizedClientService);

        var oauth2 = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2.setDefaultClientRegistrationId(props.adminClientRegistration()); // matches registration id in application.yml

        return WebClient.builder()
                .baseUrl(props.kcBaseUrl())
                .filter(oauth2)
                .build();
    }

    @Bean
    public ReactiveOAuth2AuthorizedClientService authorizedClientService(
            ReactiveClientRegistrationRepository clientRegistrations) {
        return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrations);
    }

    @Bean
    public WebClient catalogWebClient(
            StorehubProperties props,
            ReactiveClientRegistrationRepository clientRegistrations,
            ReactiveOAuth2AuthorizedClientService authorizedClientService) {

        var authorizedClientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                        clientRegistrations, authorizedClientService);

        var oauth2 = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2.setDefaultClientRegistrationId(props.internalClientRegistration()); // matches the registration in application.yml

        return WebClient.builder()
                .baseUrl(props.catalogBaseUrl()) // internal service URL
                .filter(oauth2)
                .build();
    }
}
