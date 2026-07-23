package com.abdelkhalek.storehub.order;

import com.abdelkhalek.storehub.order.order.OrderEventPublisher;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.models.OrderRequest;
import com.abdelkhalek.storehub.order.order.service.OrderService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.UUID;

@OpenAPIDefinition(
        info = @Info(
                title = "OpenAPI definition",
                version = "1.0.0"
        ),
        servers = {
                @Server(url = "http://localhost:8081", description = "Local Development"),
                @Server(url = "https://api.yourcompany.com", description = "Production")
        }
)
@Slf4j
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(OrderApplication.class, args);

        OrderEventPublisher publisher = ctx.getBean(OrderEventPublisher.class);

        /*publisher.slotReleased(UUID.fromString("bb4d3927-870c-4ee9-a1c4-afc91231ea53"))
                .block();*/

        OrderService orderService = ctx.getBean(OrderService.class);
        ReactiveJwtDecoder jwtDecoder = ctx.getBean(ReactiveJwtDecoder.class);

        // assign real values to these vars
        UUID slotId = UUID.fromString("302586d9-217c-42ae-8cfd-68ba91fdf16c");
        UUID cartId = UUID.fromString("438c924b-ccb4-4f1f-a0bb-6e55037c0fd1");
        UUID storeId = UUID.fromString("7f76f5f6-0d95-4170-a719-365e9330fe64");
        OrderRequest orderRequest = new OrderRequest(slotId, storeId, cartId,
                null, null, null, null, null);

        String rawToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJKbmx6OFVuTXFrQ1YwS3BDaXNpazI0T0dmYnFpdHE3UTNmMFRtTDAzRXowIn0.eyJleHAiOjE3ODQ3ODI0MzIsImlhdCI6MTc4NDc2NDQzMiwianRpIjoiM2Q1YzgwZjgtMjQ3My00N2ZlLTg5ODAtMTc4NzZmZDM1OTM1IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy9zdG9yZWh1YiIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI3NWE3NDEyZi1mMjYzLTQ0ZjEtOTdhYi1hZWY4Zjc1YWZjMTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJvcmRlciIsInNpZCI6ImNmZjczOTg1LWYxZGItNDI4NC1iMTY5LTI2YTUwYTE5NjcwYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiLyoiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiQ1VTVE9NRVIiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtc3RvcmVodWIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgc3RvcmVJZCBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6InRlc3QgdXNlciIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3R1c2VyQGV4YW1wbGUuY29tIiwiZ2l2ZW5fbmFtZSI6InRlc3QiLCJmYW1pbHlfbmFtZSI6InVzZXIiLCJlbWFpbCI6InRlc3R1c2VyQGV4YW1wbGUuY29tIn0.w4ad8_rAu9Vigdvk4jR40Pp6_5dEU9UaDzMMcrGskJStc9-NENhJ6lAYPfRU78wPEU1CmaG4Te6GvXZU2i5yJmwo8JlYkGeHehoYUsFz09hz_csgoAHpwdgMaV53zrAaVt8RoAQyQ_qU6ML-m16mlpoAn3K4tMBRleI0EYSBq-O1WSGljPEqDlu_4CVFPqayb3rmKKKDxzizG0m3PaaoslNDWRJR-Vn7OWkp0rMUUhoN7C--vPi7rjnhfglKT3y0g6mhVO8rDrUQwtClv7C0iL17G32IwQSyMNFiEkXV6D7RhWugejckPqbgjiC_HxMALtnPtvu598apfCQdaCVqxg";

        Jwt jwt = jwtDecoder.decode(rawToken).block();

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt);

        Order order = orderService.placeOrder(orderRequest)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken))
                .block();;

        assert order != null;
        log.info(order.toString());

    }

}
