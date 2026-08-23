package com.abdelkhalek.storehub.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;


@Slf4j
@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(OrderApplication.class, args);
            }

}
