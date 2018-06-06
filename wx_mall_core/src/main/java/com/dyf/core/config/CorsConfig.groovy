package com.dyf.core.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration()
        corsConfiguration.addAllowedOrigin("*") // 1 设置访问源地址
        corsConfiguration.addAllowedHeader("*")// 2 设置访问源请求头
        corsConfiguration.addAllowedMethod("*")// 3 设置访问源请求方法
        return corsConfiguration;
    }

    @Bean
    org.springframework.web.filter.CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", buildConfig()) // 4 对接口配置跨域设置
        return new org.springframework.web.filter.CorsFilter(source)
    }
}
