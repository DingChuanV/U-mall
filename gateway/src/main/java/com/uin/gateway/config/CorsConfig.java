package com.uin.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author wanglufei
 * @description: 使用拦截器给请求加上允许跨域
 * @date 2022/4/20/9:26 PM
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource url = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //配置跨域
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        //来源
        corsConfiguration.addAllowedOriginPattern("*");
        //cookie
        corsConfiguration.setAllowCredentials(true);
        url.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(url);
    }
}
