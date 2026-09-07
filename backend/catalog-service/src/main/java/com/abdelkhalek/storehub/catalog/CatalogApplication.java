package com.abdelkhalek.storehub.catalog;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT",
		in = SecuritySchemeIn.HEADER
)
@SpringBootApplication
public class CatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogApplication.class, args);
	}

}
