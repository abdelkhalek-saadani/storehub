package com.abdelkhalek.storehub.order;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

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
@EnableR2dbcAuditing
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(OrderApplication.class, args);
            }

}
