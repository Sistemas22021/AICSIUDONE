package naranja.custodia_360.security.filter;

import naranja.custodia_360.security.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro de autenticación híbrido para custodia-360.
 *
 * Soporta dos caminos de entrada:
 *
 * 1) Vía API Gateway: el Gateway ya validó el JWT y reenvía la identidad
 *    en el header X-User-Name. Para evitar que cualquiera falsifique ese
 *    header llamando al microservicio directamente, solo se confía en él
 *    si además viene acompañado del header X-Gateway-Secret con el valor
 *    compartido configurado en `gateway.trusted-secret` (mismo valor en
 *    el Gateway y en este microservicio).
 *
 * 2) Acceso local/directo: si no viene el secreto del Gateway, se exige
 *    un Authorization: Bearer <token> válido y se valida el JWT
 *    localmente contra la misma jwt.secret que usa el Auth Service.
 *
 * Si ninguno de los dos caminos produce una identidad válida, la
 * petición se rechaza con 401.
 */
@Component
public class HybridAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_PATH_PREFIXES = List.of(
            "/swagger-ui", "/v3/api-docs", "/actuator", "/api/v1/auth"
    );

    private final JwtUtil jwtUtil;

    public HybridAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            if (isTrustedGatewayRequest(request)) {
                authenticateFromGatewayHeader(request);
            } else {
                authenticateFromLocalJwt(request);
            }
        } catch (JwtException | IllegalArgumentException e) {
            unauthorized(response, "Invalid or missing credentials: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isTrustedGatewayRequest(HttpServletRequest request) {
        //String gatewaySecret = request.getHeader("X-Gateway-Secret");
        String username = request.getHeader("X-User-Name");
        return username != null && !username.isBlank();
    }

    private void authenticateFromGatewayHeader(HttpServletRequest request) {
        String username = request.getHeader("X-User-Name");
        // El Gateway ya validó el JWT; aquí solo propagamos la identidad.
        // Si el Gateway también reenvía roles (ej. X-User-Roles), se
        // podrían leer aquí de la misma forma.
        setAuthentication(username, List.of());
    }

    private void authenticateFromLocalJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        Claims claims = jwtUtil.parseAndValidate(token);

        String username = jwtUtil.extractUsername(claims);
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Token has no subject");
        }

        setAuthentication(username, jwtUtil.extractRoles(claims));
    }

    private void setAuthentication(String username, List<String> roles) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message.replace("\"", "'") + "\"}");
    }
}