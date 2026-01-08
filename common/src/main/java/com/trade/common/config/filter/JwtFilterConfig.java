package com.trade.common.config.filter;

import com.trade.common.component.JwtProvider;
import com.trade.common.filter.JwtAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class JwtFilterConfig {

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter(JwtProvider jwtProvider) {
        FilterRegistrationBean<JwtAuthFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new JwtAuthFilter(jwtProvider, List.of(
                "/v1/api/auth/login",
                "/v1/api/auth/signup",
                "/v1/api/auth/refresh",
                "/actuator",
                "/health"
        )));
        bean.addUrlPatterns("/*");
        bean.setOrder(20);
        return bean;
    }
}