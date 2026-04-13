package com.sso.gateway.config;

import com.sso.gateway.config.JwtAuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de rutas del API Gateway.
 *
 * <p>Define las rutas en código Java (no solo en YAML) para mayor
 * visibilidad y facilitar los tests. Las rutas en YAML se pueden
 * usar también — se complementan.
 *
 * <p><b>Convención de URLs:</b>
 * <ul>
 *   <li>{@code /api/v1/auth/**} → auth-service (Eureka: AUTH-SERVICE)</li>
 *   <li>Los endpoints públicos no aplican el filtro JWT</li>
 *   <li>Los endpoints protegidos pasan por {@code JwtAuthFilter}</li>
 * </ul>
 */
@Configuration
public class GatewayConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public GatewayConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // ─── Auth Service — Rutas PÚBLICAS (login, registro, refresh) ───
                // No aplican el filtro JWT: el usuario todavía no tiene token
                .route("auth-service-public", r -> r
                        .path("/api/v1/auth/login",
                              "/api/v1/auth/register",
                              "/api/v1/auth/refresh")
                        .uri("lb://AUTH-SERVICE")) // lb:// usa Eureka para load balancing

                // ─── Auth Service — Swagger (solo dev) ───────────────────────────
                .route("auth-service-docs", r -> r
                        .path("/auth/swagger-ui/**",
                              "/auth/v3/api-docs/**")
                        .filters(f -> f.stripPrefix(1)) // Quita el prefijo /auth
                        .uri("lb://AUTH-SERVICE"))

                // ─── Auth Service — Rutas PROTEGIDAS ────────────────────────────
                // El filtro JWT valida el token antes de pasar la petición
                .route("auth-service-protected", r -> r
                        .path("/api/v1/auth/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("lb://AUTH-SERVICE"))

                // ─── Aquí otros equipos agregarán sus servicios ──────────────────
                // Ejemplo:
                // .route("team-alpha-products", r -> r
                //         .path("/api/v1/products/**")
                //         .filters(f -> f.filter(jwtAuthFilter))
                //         .uri("lb://PRODUCTS-SERVICE"))

                .build();
    }
}
