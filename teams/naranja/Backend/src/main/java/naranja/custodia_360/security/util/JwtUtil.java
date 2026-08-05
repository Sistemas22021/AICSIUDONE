package naranja.custodia_360.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Valida y parsea JWTs emitidos por el Auth Service central.
 * Se usa cuando la petición llega directamente al microservicio
 * (sin pasar por el API Gateway) y por lo tanto no trae X-User-Name.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public Claims parseAndValidate(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (claims.getExpiration() == null || claims.getExpiration().before(new Date())) {
            throw new JwtException("Token expired");
        }

        return claims;
    }

    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

    public List<String> extractRoles(Claims claims) {
        String rolesClaim = claims.get("roles", String.class);
        if (rolesClaim == null || rolesClaim.isBlank()) {
            return List.of();
        }
        return Arrays.asList(rolesClaim.split(","));
    }
}