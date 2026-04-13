package com.sso.auth.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Swagger / OpenAPI 3.
 *
 * <p>Disponible en: {@code http://localhost:8080/swagger-ui.html}
 *
 * <p>Incluye el esquema de seguridad Bearer JWT para que los estudiantes
 * puedan probar los endpoints protegidos directamente desde la UI.
 */
@Configuration
public class SwaggerConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI ssoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SSO Boilerplate — Auth Service API")
                        .description("""
                                API de autenticación centralizada del ecosistema SSO.
                                
                                **Flujo de uso:**
                                1. `POST /api/v1/auth/register` — Crear cuenta
                                2. `POST /api/v1/auth/login` — Obtener accessToken + refreshToken (Cookie)
                                3. Usar el `accessToken` como Bearer en los endpoints protegidos
                                4. `POST /api/v1/auth/refresh` — Renovar accessToken usando el Cookie
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Main — SSO Boilerplate")
                                .url("https://github.com/tu-org/sso-boilerplate"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
                // Permite autenticarse en Swagger UI con el Bearer token
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Pegar el accessToken obtenido del endpoint /login")));
    }
}
