package com.trade.auth;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication(scanBasePackages = {"com.trade.auth", "com.trade.common"})
@EnableJpaAuditing
@ConfigurationPropertiesScan(basePackages = "com.trade.common.config")
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }
}
