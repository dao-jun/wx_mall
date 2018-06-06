package com.dyf.apiadmin.config

import com.dyf.apiadmin.annotions.support.LoginAdminHandlerMethodArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@Configuration
class AdminConfig extends WebMvcConfigurerAdapter {
    @Override
    void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new LoginAdminHandlerMethodArgumentResolver())
    }
}
