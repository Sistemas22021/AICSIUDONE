package naranja.custodia_360.security.config;

import naranja.custodia_360.security.filter.HybridAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // habilita @PreAuthorize en los controllers
public class SecurityConfig {

    /**
     * HybridAuthenticationFilter está anotado con @Component, así que
     * Spring Boot lo auto-registraría también como filtro GLOBAL del
     * servlet container (fuera de la cadena de Spring Security), además
     * de la copia que se agrega abajo con addFilterBefore. Eso hace que
     * la autenticación que setea nunca la vea FilterChainProxy y todo
     * caiga a AnonymousAuthenticationFilter -> 403.
     *
     * Este bean desactiva ese auto-registro global para que SOLO exista
     * la instancia que va dentro de la cadena de seguridad.
     */
    @Bean
    public FilterRegistrationBean<HybridAuthenticationFilter> disableAutoRegistration(
            HybridAuthenticationFilter filter) {
        FilterRegistrationBean<HybridAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    private final HybridAuthenticationFilter hybridAuthenticationFilter;

    public SecurityConfig(HybridAuthenticationFilter hybridAuthenticationFilter) {
        this.hybridAuthenticationFilter = hybridAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {}) // usa el CorsConfigurationSource del bean corsConfigurationSource
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/swagger-ui/**", "/v3/api-docs/**", "/actuator/**", "/api/v1/auth/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(hybridAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}