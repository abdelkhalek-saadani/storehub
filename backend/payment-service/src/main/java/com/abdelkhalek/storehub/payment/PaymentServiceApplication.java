package com.abdelkhalek.storehub.payment;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
@SpringBootApplication
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}

}
