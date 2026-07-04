package equipoBlanco.com.prison_service.common.config;

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

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SIGP - Prison Service API")
                        .version("1.0.0")
                        .description("API del Sistema Integral de Gestión Penitenciaria (SIGP) - Módulo de Prisiones. " +
                                "Proporciona endpoints para gestionar reclusos, celdas, mapas penitenciarios, " +
                                "traslados, expedientes post-penitenciarios, calendario de presentaciones, " +
                                "incidentes y reportes de fallecimiento.")
                        .contact(new Contact()
                                .name("Equipo Blanco"))
                        .license(new License()
                                .name("MIT")))
                .addSecurityItem(new SecurityRequirement().addList("HeaderAuth"))
                .schemaRequirement("HeaderAuth", new SecurityScheme()
                        .name("HeaderAuth")
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .description("Autenticación mediante headers X-User-Name y X-User-Role")
                        .extensions(java.util.Map.of(
                                "x-header-name", "X-User-Name",
                                "x-header-role", "X-User-Role"
                        )));
    }
}
