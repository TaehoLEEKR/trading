package com.trade.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.trade.common.config")
public class CommonApplication {
    public static void main(String[] args){
        SpringApplication.run(CommonApplication.class, args);
    }
}
