package com.trade.md;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"com.trade.common","com.trade"})
@ConfigurationPropertiesScan(basePackages = "com.trade.common.config")
//@MapperScan("com.trade.catalog.mapper")
@MapperScan(basePackages = {
        "com.trade.md.mapper",
        "com.trade.catalog.mapper"
})
public class MdApplication {
    public static void main(String[] args) {
        SpringApplication.run(MdApplication.class, args);
    }
}
