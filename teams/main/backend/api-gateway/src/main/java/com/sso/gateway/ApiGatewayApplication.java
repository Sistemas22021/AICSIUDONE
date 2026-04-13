package com.sso.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del API Gateway.
 *
 * <p>El Gateway es el único punto de entrada al ecosistema de microservicios.
 * Sus responsabilidades son:
 * <ul>
 *   <li>Ruteo de peticiones hacia los microservicios correctos</li>
 *   <li>Validación del JWT en cada petición protegida</li>
 *   <li>Centralización de CORS (los servicios individuales no necesitan configurarlo)</li>
 * </ul>
 *
 * <p><b>Nota técnica:</b> Spring Cloud Gateway usa WebFlux (reactivo / non-blocking).
 * Por eso los filtros deben implementar {@code GatewayFilter}, no {@code Filter} de Servlet.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
