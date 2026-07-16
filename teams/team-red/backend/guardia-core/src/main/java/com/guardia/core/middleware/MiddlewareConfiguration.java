package com.guardia.core.middleware;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MiddlewareConfiguration implements WebMvcConfigurer {

    private final MiddlewareInterceptor middlewareInterceptor;

    @Autowired
    public MiddlewareConfiguration(MiddlewareInterceptor middlewareInterceptor) {
        this.middlewareInterceptor = middlewareInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(middlewareInterceptor)
                .addPathPatterns("/api/v1/**")
                .order(Integer.MIN_VALUE + 1);
    }
}