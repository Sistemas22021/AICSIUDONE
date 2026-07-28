package com.guardia.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * Nombre del security scheme, referenciado por el requirement global de
     * abajo. Coincide con lo que MiddlewareInterceptor ya exige en cada
     * request real: header "Authorization: Bearer <JWT>".
     */
    private static final String ESQUEMA_JWT = "bearerAuth";

    @Bean
    public OpenAPI guardiaCoreOpenAPI() {

        return new OpenAPI()

                .info(new Info()

                        .title("Guardia Core API")

                        .description("""
                                API REST del Sistema Inteligente para la Gestión
                                y Análisis Criminal desarrollado para la UDO.

                                Esta documentación permite probar todos los
                                endpoints disponibles del sistema.

                                Para autenticarte: obtén un JWT iniciando sesión en
                                el auth-service (POST /api/v1/auth/login) y pégalo
                                en el botón "Authorize" 🔓 de arriba a la derecha
                                (solo el token, sin el prefijo "Bearer " — Swagger
                                lo agrega automáticamente en cada petición).
                                """)

                        .version("1.0.0")

                        .contact(new Contact()
                                .name("Team Ranger")
                                .email("teamranger@udo.edu")
                        )

                        .license(new License()
                                .name("Uso Académico")
                        )
                )

                .externalDocs(new ExternalDocumentation()
                        .description("Repositorio del Proyecto")
                        .url("https://github.com/Sistemas22021/AICSIUDONE"))

                // Define CÓMO se autentica un endpoint (JWT Bearer) y habilita
                // el botón "Authorize" en Swagger UI.
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_JWT, new SecurityScheme()
                                .name(ESQUEMA_JWT)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT emitido por el auth-service (SSO). Pega solo el token, sin \"Bearer \".")))

                // Aplica ese esquema a TODOS los endpoints por defecto, incluidos
                // los que se agreguen en el futuro — no hace falta anotar cada
                // controlador nuevo con @SecurityRequirement para que aparezca
                // el candado y el botón "Try it out" mande el token.
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_JWT));
    }
}