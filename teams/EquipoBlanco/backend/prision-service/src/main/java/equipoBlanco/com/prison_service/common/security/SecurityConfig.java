package equipoBlanco.com.prison_service.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // ─── Celdas ───
                // Administrador del Sistema: CRUD completo de celdas
                .requestMatchers(HttpMethod.POST, "/api/v1/cells").hasRole("ADMINISTRADOR_DEL_SISTEMA")
                .requestMatchers(HttpMethod.PUT, "/api/v1/cells/**").hasRole("ADMINISTRADOR_DEL_SISTEMA")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/cells/**").hasRole("ADMINISTRADOR_DEL_SISTEMA")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/cells/*/position").hasRole("ADMINISTRADOR_DEL_SISTEMA")
                // Asignar recluso a celda: Oficial Penitenciario
                .requestMatchers(HttpMethod.POST, "/api/v1/cells/*/assign/**").hasRole("OFICIAL_PENITENCIARIO")
                // Ver celdas: Oficial Penitenciario, Supervisor
                .requestMatchers(HttpMethod.GET, "/api/v1/cells/**").hasAnyRole("OFICIAL_PENITENCIARIO", "SUPERVISOR")

                // ─── Mapas ───
                .requestMatchers(HttpMethod.PUT, "/api/v1/maps/**").hasRole("ADMINISTRADOR_DEL_SISTEMA")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/maps/**").hasRole("ADMINISTRADOR_DEL_SISTEMA")
                .requestMatchers(HttpMethod.GET, "/api/v1/maps/**").hasAnyRole("OFICIAL_PENITENCIARIO", "SUPERVISOR")
                .requestMatchers(HttpMethod.POST, "/api/v1/maps/**").hasRole("ADMINISTRADOR_DEL_SISTEMA")

                // ─── Reclusos (inmates) ───
                .requestMatchers(HttpMethod.POST, "/api/v1/inmates").hasRole("OFICIAL_PENITENCIARIO")
                .requestMatchers(HttpMethod.POST, "/api/v1/inmates/*/discharge").hasAnyRole("OFICIAL_PENITENCIARIO", "SUPERVISOR")
                .requestMatchers(HttpMethod.GET, "/api/v1/inmates/**").hasAnyRole("OFICIAL_PENITENCIARIO", "SUPERVISOR")

                // ─── Traslados ───
                .requestMatchers(HttpMethod.POST, "/api/v1/transfers").hasRole("OFICIAL_PENITENCIARIO")
                .requestMatchers(HttpMethod.PUT, "/api/v1/transfers/*/resolve").hasRole("SUPERVISOR")
                .requestMatchers(HttpMethod.GET, "/api/v1/transfers/**").hasAnyRole("OFICIAL_PENITENCIARIO", "SUPERVISOR")

                // ─── Post-Penitenciario ───
                .requestMatchers(HttpMethod.POST, "/api/v1/post-penal/expedientes/*/assign").hasRole("SUPERVISOR")
                .requestMatchers(HttpMethod.GET, "/api/v1/post-penal/**").hasAnyRole("OFICIAL_DE_SEGUIMIENTO", "SUPERVISOR")
                .requestMatchers(HttpMethod.POST, "/api/v1/post-penal/**").hasAnyRole("OFICIAL_DE_SEGUIMIENTO", "SUPERVISOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/post-penal/**").hasAnyRole("OFICIAL_DE_SEGUIMIENTO", "SUPERVISOR")

                // ─── Calendario ───
                .requestMatchers(HttpMethod.POST, "/api/v1/calendario/**").hasAnyRole("OFICIAL_DE_SEGUIMIENTO", "SUPERVISOR")
                .requestMatchers(HttpMethod.PUT, "/api/v1/calendario/**").hasAnyRole("OFICIAL_DE_SEGUIMIENTO", "SUPERVISOR")
                .requestMatchers(HttpMethod.GET, "/api/v1/calendario/**").hasAnyRole("OFICIAL_DE_SEGUIMIENTO", "SUPERVISOR")

                // ─── Actuator / health ───
                .requestMatchers("/actuator/**").permitAll()

                // Cualquier otra solicitud (por defecto denegada)
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("X-User-Name", "X-User-Role"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
