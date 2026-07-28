package com.abdelkhalek.storehub.order;

import com.abdelkhalek.storehub.order.order.OrderEventPublisher;
import com.abdelkhalek.storehub.order.order.dto.OrderCreatedResponse;
import com.abdelkhalek.storehub.order.order.dto.OrderRequest;
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

        // assign real values to these vars (get the values from catalog db)
        UUID slotId = UUID.fromString("302586d9-217c-42ae-8cfd-68ba91fdf16c");
        UUID cartId = UUID.fromString("438c924b-ccb4-4f1f-a0bb-6e55037c0fd1");
        UUID storeId = UUID.fromString("7f76f5f6-0d95-4170-a719-365e9330fe64");
        UUID idemKey = UUID.fromString("9bec2ffb-5d51-4e46-bc27-0a1117acf7ab");
        OrderRequest orderRequest = new OrderRequest(slotId, storeId, cartId,
                null, null, null,
                null, null,idemKey);

        // testuser@example.com:testuser
        String rawToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJKbmx6OFVuTXFrQ1YwS3BDaXNpazI0T0dmYnFpdHE3UTNmMFRtTDAzRXowIn0.eyJleHAiOjE3ODUyNjg2NjAsImlhdCI6MTc4NTI1MDY2MCwianRpIjoiMzdkNDczOTMtMGM2OC00MmU0LTk3YzMtMWNhYTdmNTRiNmNjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgxL3JlYWxtcy9zdG9yZWh1YiIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI3NWE3NDEyZi1mMjYzLTQ0ZjEtOTdhYi1hZWY4Zjc1YWZjMTMiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJvcmRlciIsInNpZCI6IjY4YjZkODUzLTEzMWEtNDMwNy04YmI2LTFlOGFmOWIyNjA2MCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiLyoiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiQ1VTVE9NRVIiLCJ1bWFfYXV0aG9yaXphdGlvbiIsImRlZmF1bHQtcm9sZXMtc3RvcmVodWIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgc3RvcmVJZCBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6InRlc3QgdXNlciIsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3R1c2VyQGV4YW1wbGUuY29tIiwiZ2l2ZW5fbmFtZSI6InRlc3QiLCJmYW1pbHlfbmFtZSI6InVzZXIiLCJlbWFpbCI6InRlc3R1c2VyQGV4YW1wbGUuY29tIn0.bJQPkgrKTv0XQz0Mw3x7o-R6kWvp0hPI3vK58zC2rlJADSODhZCwRgq8V-kgvqthwLCU_J2xFoV6RjpZ1RVOCyNO3Pfffm-ljxA8kQtlEdUVdnGQxqEJthcj999nfz6piBMCCLgyee-IeYbRRUdoCZiow_yiHLsFp7UwDPiSAcgO8JIeKge1cDUodsDplBAkg4L21E4uISwgJ2V-QnrR74RKmfFH9v212kzQimtUaAA4sT1PuC4eb6fECtThgZzp9-lFDarncR9uTIupXv_pYU3arGQLzPCi7u3HIjXv7BrFbkA5elJaSo2mnx0DfTGfRPjRH2fKcdt67qT72ESTaw";

        Jwt jwt = jwtDecoder.decode(rawToken).block();

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt);

        OrderCreatedResponse order = orderService.placeOrderWithOnlinePayment(orderRequest)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken))
                .block();;

        assert order != null;
        log.info(order.toString());

    }

}
