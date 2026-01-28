package com.trade.run;

import com.trade.common.util.Shutdown;
import com.trade.run.config.RunProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.trade"})
@EnableConfigurationProperties(RunProperties.class)
@EnableScheduling
@EnableJpaAuditing
@EnableAsync
public class RunApplication {
    public static void main(String[] args) {
        SpringApplication.run(RunApplication.class, args);
    }
    @Bean
    public Shutdown gracefulShutdown() {
        return new Shutdown();
    }

    @Bean
    public ServletWebServerFactory servletContainer(final Shutdown gracefulShutdown) {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addConnectorCustomizers(gracefulShutdown);
        return tomcat;
    }
}
