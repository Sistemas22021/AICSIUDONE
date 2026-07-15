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
                .addSecurityItem(new SecurityRequirement()
                        .addList("UserNameHeader")
                        .addList("UserRoleHeader"))
                .schemaRequirement("UserNameHeader", new SecurityScheme()
                        .name("X-User-Name")
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .description("Nombre del usuario"))
                .schemaRequirement("UserRoleHeader", new SecurityScheme()
                        .name("X-User-Role")
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .description("Rol del usuario (ADMINISTRADOR_DEL_SISTEMA, OFICIAL_PENITENCIARIO, SUPERVISOR, OFICIAL_DE_SEGUIMIENTO)"));
    }
}
