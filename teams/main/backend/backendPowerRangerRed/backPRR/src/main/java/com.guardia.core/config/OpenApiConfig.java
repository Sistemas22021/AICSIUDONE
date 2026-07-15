package com.guardia.core.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

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
                        .url("https://github.com/Sistemas22021/AICSIUDONE"));
    }
}