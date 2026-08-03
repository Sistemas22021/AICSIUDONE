package com.ccc.sistema_balistico.core.infrastructure.in.web.config;

import com.ccc.sistema_balistico.core.infrastructure.in.web.middleware.MiddlewareInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración MVC del servicio balístico.
 * Registra el interceptor con la Cadena de Responsabilidad y habilita CORS para el frontend MFE.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final MiddlewareInterceptor middlewareInterceptor;

    public WebMvcConfig(MiddlewareInterceptor middlewareInterceptor) {
        this.middlewareInterceptor = middlewareInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(middlewareInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/error/**",
                        "/actuator/**"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
