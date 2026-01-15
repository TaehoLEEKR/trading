package com.trade.common.filter.internal;

import com.trade.common.config.InternalAuthProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(InternalAuthProperties.class)
@ConditionalOnProperty(prefix = "internal.auth", name = "enabled", havingValue = "true")
public class InternalAuthAutoConfig {

    @Bean
    public FilterRegistrationBean<InternalTokenFilter> internalTokenFilter(InternalAuthProperties props) {
        FilterRegistrationBean<InternalTokenFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new InternalTokenFilter(props));
        bean.setOrder(0);
        return bean;
    }
}