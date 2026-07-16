package com.nexocriminal.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI nexoCriminalOpenAPI() {
        final String schemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Nexo Criminal API")
                        .description("API REST de la plataforma de inteligencia criminal. " +
                                "Gestión de personas, vehículos, sucesos, desapariciones, " +
                                "motor de detección Red Thread y análisis con IA.")
                        .version("1.0.0")
                        .contact(new Contact().name("Equipo Amarillo — UDO Nueva Esparta")))
                // Definir el esquema de seguridad JWT (Bearer)
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components().addSecuritySchemes(schemeName,
                        new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}