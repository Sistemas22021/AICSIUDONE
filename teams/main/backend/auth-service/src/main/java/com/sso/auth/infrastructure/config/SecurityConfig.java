package com.sso.auth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración de seguridad del Auth Service.
 *
 * <p><b>Decisiones clave:</b>
 * <ul>
 *   <li>Stateless: no se guarda sesión en el servidor (JWT auto-contenido)</li>
 *   <li>CSRF deshabilitado: no aplica en APIs REST stateless</li>
 *   <li>Los endpoints de autenticación son públicos (register, login, refresh)</li>
 *   <li>El resto requiere autenticación (aunque el Gateway ya valida el token)</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // API REST stateless: no necesita sesión HTTP
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // CSRF no aplica en APIs JWT stateless
            .csrf(AbstractHttpConfigurer::disable)

            // CORS: permite peticiones desde el Login MFE y Consumer App
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Rutas públicas y protegidas
            .authorizeHttpRequests(auth -> auth
                    // Swagger UI — solo en dev (se puede restringir por perfil)
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                    // Endpoints de autenticación — sin token
                    .requestMatchers("/api/v1/auth/register",
                                     "/api/v1/auth/login",
                                     "/api/v1/auth/refresh").permitAll()
                    // Health check de Actuator
                    .requestMatchers("/actuator/health").permitAll()
                    // Todo lo demás requiere autenticación
                    .anyRequest().authenticated()
            );

        return http.build();
    }

    /**
     * BCrypt con strength 10 (balance entre seguridad y rendimiento).
     * Strength 12+ es más seguro pero puede ser lento en t2.micro.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * CORS: permite peticiones desde los frontends del ecosistema.
     * En producción, reemplazar "*" por las URLs reales de S3/Vercel.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",      // Desarrollo local
                "https://*.vercel.app",    // Vercel (todos los deployments)
                "https://*.s3-website-*.amazonaws.com" // S3 static hosting
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // Necesario para enviar Cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
