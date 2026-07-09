package com.azulcian.GestionIncidentesPatrullas.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI gestionIncidentesOpenAPI() {

        return new OpenAPI()

                .info(new Info()

                        .title("Sistema de Gestión de Incidentes en Tiempo Real y Despacho de Patrullas")

                        .description("""
                                API REST para administrar incidentes policiales, patrullas y asignaciones operativas.

                                Funcionalidades principales:
                                • Registro de incidentes.
                                • Administración de patrullas.
                                • Asignación automática de patrullas.
                                • Actualización de estados.
                                • Resumen estadístico para dashboard.
                                """)

                        .version("1.0")

                        .contact(new Contact()
                                .name("Equipo Azul Cian")
                                .email("grupocian.sistemas2@gmail.com"))

                        .license(new License()
                                .name("Proyecto Académico")))

                .externalDocs(new ExternalDocumentation()
                        .description("Sistema desarrollado para la asignatura Sistemas de Información II"));
    }

}
