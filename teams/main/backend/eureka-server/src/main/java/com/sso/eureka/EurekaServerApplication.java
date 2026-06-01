package com.sso.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Punto de entrada del Eureka Server.
 *
 * <p>Eureka actúa como "directorio telefónico" de los microservicios.
 * Cada servicio se registra aquí al arrancar, y otros servicios lo
 * consultan para encontrar la ubicación (host:puerto) de sus dependencias.
 *
 * <p><b>¿Por qué es el primer servicio en levantarse?</b><br>
 * Config Server, Auth Service y API Gateway necesitan registrarse en Eureka
 * para que el Gateway pueda encontrarlos dinámicamente. Sin Eureka activo,
 * el resto de los servicios fallarán al iniciar.
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
