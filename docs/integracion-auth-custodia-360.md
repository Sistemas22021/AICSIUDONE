# Integración de autenticación con el ecosistema SSO

Este documento describe de forma general cómo conectar cualquier microservicio y frontend con el sistema de autenticación centralizado del ecosistema.

## Objetivo

Permitir que cualquier equipo pueda:

- autenticar usuarios desde un frontend,
- recibir y usar un token JWT de acceso,
- proteger sus endpoints con el API Gateway,
- identificar al usuario dentro de su microservicio de forma consistente.

---

## 1. Arquitectura general

El flujo recomendado es el siguiente:

1. El frontend solicita autenticación al Auth Service.
2. El Auth Service valida las credenciales y devuelve un access token JWT.
3. El API Gateway valida el token para las rutas protegidas.
4. El microservicio destino recibe la identidad del usuario a través de un header, por ejemplo `X-User-Name`.
5. El frontend usa el access token para llamar a los endpoints protegidos.

---

## 2. Qué debe implementar cada parte

### 2.1 Frontend

Todo frontend que participe del flujo debe:

- iniciar el login contra el Auth Service o el punto de entrada del SSO,
- guardar el access token de forma segura en memoria o en un contexto global,
- adjuntar el token en cada petición protegida con `Authorization: Bearer <token>`,
- enviar `credentials: 'include'` cuando el flujo use cookies HttpOnly.

### 2.2 API Gateway

El API Gateway es el componente que:

- recibe todas las peticiones que llegan al ecosistema,
- valida el JWT antes de permitir el acceso a rutas protegidas,
- rechaza peticiones sin token válido con `401 Unauthorized`,
- pasa la identidad del usuario al microservicio destino mediante headers como `X-User-Name`.

### 2.3 Microservicio

Todo microservicio que quiera participar del esquema debe:

- aceptar peticiones solo a través del Gateway o de una ruta configurada para ello,
- leer el header `X-User-Name` para identificar al usuario,
- usar esa identidad para autorizar operaciones o consultar datos del usuario,
- devolver `401` cuando no exista una identidad válida.

---

## 3. Integración del frontend

### 3.1 Flujo de login

Cuando el usuario entra al sistema:

1. el frontend verifica si existe un access token válido,
2. si no existe, inicia el proceso de login,
3. el Auth Service devuelve un token de acceso,
4. el frontend guarda ese token en memoria o en un contexto global,
5. el frontend continúa navegando dentro del sistema protegido.

### 3.2 Ejemplo general de login

Este ejemplo asume que el Auth Service devuelve el `accessToken` directamente en el cuerpo de la respuesta.

```ts
import axios from 'axios'; // Se recomienda usar Axios para peticiones HTTP

interface LoginResponse {
  accessToken: string;
  // Otros datos como refreshToken, expiresIn, etc.
}

async function login(username: string, password: string): Promise<string> {
  const response = await axios.post<LoginResponse>('http://localhost:8090/api/v1/auth/login', {
    username,
    password
  }, {
    withCredentials: true // Importante para enviar cookies HttpOnly (refresh token)
  });

  return response.data.accessToken;
}
```

### 3.2.1 Redirección al Login MFE (Frontend)

Cuando un micro-frontend (MFE) detecta que no hay un token de acceso válido o que la sesión ha expirado, debe redirigir al usuario al Login MFE centralizado. Esto se hace pasando la URL actual del MFE como un parámetro de redirección.

**Ejemplo conceptual de `AuthGuard` en React (usando React Router):**

```tsx
// src/guards/AuthGuard.tsx
import React, { useContext, useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext'; // Asume un contexto de autenticación
import { verifyToken } from '../utils/auth'; // Función para verificar la validez del token

interface AuthGuardProps {
  children: React.ReactNode;
}

const AuthGuard: React.FC<AuthGuardProps> = ({ children }) => {
  const { accessToken, setAccessToken } = useContext(AuthContext);
  const navigate = useNavigate();
  const location = useLocation();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const checkAuth = async () => {
      setIsLoading(true);
      let currentToken = accessToken;

      // 1. Intentar obtener el token de la URL (después de un login exitoso desde el MFE de Login)
      const params = new URLSearchParams(location.search);
      const tokenFromUrl = params.get('token');

      if (tokenFromUrl) {
        setAccessToken(tokenFromUrl);
        currentToken = tokenFromUrl;
        // Limpiar la URL para que el token no quede expuesto
        navigate(location.pathname, { replace: true });
      }

      // 2. Verificar si tenemos un token válido
      if (currentToken && verifyToken(currentToken)) {
        setIsLoading(false);
        return;
      }

      // 3. Si no hay token o es inválido, redirigir al Login MFE
      const loginMfeUrl = import.meta.env.VITE_LOGIN_MFE_URL || 'http://localhost:3000'; // URL del Login MFE
      const currentAppUrl = window.location.origin + location.pathname;
      window.location.href = `${loginMfeUrl}?redirect=${encodeURIComponent(currentAppUrl)}`;
    };

    checkAuth();
  }, [accessToken, location.search, navigate, setAccessToken]);

  if (isLoading) {
    return <div>Cargando sesión...</div>; // O un spinner
  }

  return <>{children}</>;
};

export default AuthGuard;
```

**Nota:** La función `verifyToken` (que verifica la firma y expiración del JWT) y el `AuthContext` deben ser implementados por cada equipo de frontend. La URL del Login MFE (`VITE_LOGIN_MFE_URL`) debe configurarse como una variable de entorno.

```ts
async function login(username: string, password: string) {
  const response = await fetch('http://localhost:8090/api/v1/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ username, password })
  });

  if (!response.ok) {
    throw new Error('Error de autenticación');
  }

  const data = await response.json();
  return data.accessToken;
}
```

### 3.3 Guardar el token

Se recomienda:

- guardar el access token en memoria o en un contexto global para evitar exposición,
- no guardar tokens sensibles en `localStorage` si se evita,
- usar cookies HttpOnly cuando el backend lo permita.

### 3.4 Enviar el token en peticiones protegidas

```ts
async function fetchProtected(url: string) {
  const token = getAccessToken();

  return fetch(url, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    },
    credentials: 'include'
  });
}
```

### 3.5 Manejo de refresh token

Cuando el access token expire:

1. el frontend llama al endpoint de refresh,
2. el backend devuelve un nuevo access token,
3. el frontend reemplaza el token existente.

Ejemplo general:

```ts
async function refreshToken() {
  const response = await fetch('http://localhost:8090/api/v1/auth/refresh', {
    method: 'POST',
    credentials: 'include'
  });

  if (!response.ok) {
    throw new Error('No se pudo refrescar el token');
  }

  const data = await response.json();
  return data.accessToken;
}
```

---

## 4. Integración del backend

### 4.1 Exponer endpoints protegidos

Cada microservicio debe definir sus rutas bajo un prefijo claro, por ejemplo:

- `/api/v1/servicio-a/**`
- `/api/v1/servicio-b/**`
- `/api/v1/servicio-c/**`

### 4.2 Leer la identidad del usuario

El API Gateway valida el token JWT y puede pasar el username al microservicio mediante el header `X-User-Name`.

En un microservicio Spring Boot, el patrón recomendado es este:

```java
@GetMapping("/me")
public ResponseEntity<?> getCurrentUser(
    @RequestHeader(value = "X-User-Name", required = false) String username) {

    if (username == null || username.isBlank()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    return ResponseEntity.ok(Map.of("username", username));
}
```

### 4.3 Qué debe colocar en el microservicio para que funcione

Para que la integración funcione correctamente, el microservicio debe hacer lo siguiente:

1. Definir un endpoint protegido.
2. Leer el header `X-User-Name`.
3. Si el header está ausente o vacío, responder con `401 Unauthorized`.
4. Si el header está presente, usar ese valor como identidad del usuario autenticado.

Ejemplo de implementación mínima:

```java
@GetMapping("/me")
public ResponseEntity<?> getCurrentUser(
    @RequestHeader(value = "X-User-Name", required = false) String username) {

    if (username == null || username.isBlank()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    return ResponseEntity.ok(Map.of("username", username));
}
```

### 4.4 Validar autenticación

Si el microservicio necesita validar el token de forma local, debe:

- usar la misma firma JWT que usa Auth Service,
- verificar expiración, firma y claims,
- rechazar peticiones inválidas con `401 Unauthorized`.

### 4.5 Configurar CORS

En desarrollo, el backend debe permitir peticiones desde los frontends locales. Ejemplo general:

```java
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

### 4.5 Seguridad recomendada

- proteger todas las rutas sensibles,
- usar roles cuando aplique,
- nunca confiar únicamente en el frontend,
- validar en backend siempre.

---

## 5. Integración con el API Gateway

El API Gateway es el punto central de entrada para las peticiones autenticadas.

### Qué hace el Gateway

- recibe las peticiones del frontend,
- valida el JWT antes de permitir pasar a un microservicio,
- si el token es válido, agrega el header `X-User-Name`,
- si el token es inválido, responde con `401 Unauthorized`.

### Qué debe configurarse en el Gateway

- rutas públicas: login, registro, refresh, health checks,
- rutas protegidas: todas las APIs que requieran autenticación,
- regla de reenvío al microservicio correspondiente.

El API Gateway utiliza un filtro para interceptar las peticiones, validar el JWT y añadir los headers de usuario.

**Ejemplo de configuración de rutas y filtro JWT en Spring Cloud Gateway:**
### Ejemplo general de configuración de rutas

- `/api/v1/auth/**` → Auth Service
- `/api/v1/servicio-a/**` → microservicio A
- `/api/v1/servicio-b/**` → microservicio B

### Importante

```java
// src/main/java/com/sso/gateway/config/GatewayConfig.java
package com.sso.gateway.config;

import com.sso.gateway.filter.JwtAuthenticationFilter; // Tu filtro JWT
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Rutas públicas que no requieren autenticación (ej. login, registro)
                .route("auth-service-public", r -> r.path("/api/v1/auth/**")
                        .uri("lb://AUTH-SERVICE"))
                // Rutas protegidas que pasan por el filtro JWT
                // Este es un ejemplo general, puedes tener rutas más específicas
                .route("protected-apis", r -> r.path("/api/v1/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter)) // Aplica el filtro JWT
                        .uri("lb://NO-OP")) // NO-OP o un servicio de fallback si no hay match específico
                // Ejemplo de ruta específica para un microservicio (con filtro aplicado)
                .route("team-alpha-products", r -> r.path("/api/v1/products/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://PRODUCTS-SERVICE"))
                // Añade más rutas para otros microservicios aquí
                .build();
    }
}
```

**Ejemplo de `JwtAuthenticationFilter` en Spring Cloud Gateway:**

```java
// src/main/java/com/sso/gateway/filter/JwtAuthenticationFilter.java
package com.sso.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    @Value("${jwt.secret}") // La misma clave secreta que usa el Auth Service
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Lógica para excluir rutas públicas si no se manejan en GatewayConfig
        // final List<String> apiEndpoints = List.of("/api/v1/auth/login", "/api/v1/auth/register", "/actuator/**");
        // Predicate<ServerHttpRequest> isApiSecured = r -> apiEndpoints.stream().noneMatch(uri -> r.getURI().getPath().contains(uri));

        // Asumimos que GatewayConfig ya filtró las rutas públicas.
        // Si la ruta llega aquí, se considera protegida.
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return this.onError(exchange, "No Authorization header or invalid format", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7); // Eliminar "Bearer "
        try {
            Claims claims = validateToken(token);
            // Añadir el username al header para el microservicio destino
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Name", claims.getSubject())
                    .build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            return this.onError(exchange, "Unauthorized: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    private Claims validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Verificar expiración
        if (claims.getExpiration().before(new Date())) {
            throw new RuntimeException("Token expired");
        }
        return claims;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}
```

**Importante:**

Si una ruta protegida no pasa por el Gateway, el microservicio debe validar el JWT por sí mismo; de lo contrario, no habrá forma de asegurar la autenticación.

### 5.1 Validación JWT Local en Microservicios (sin Gateway)

Si un microservicio no está detrás del API Gateway o necesita una validación JWT más granular, debe realizar la validación del token por sí mismo. Esto implica usar una librería JWT (como `jjwt` en Java) para parsear, verificar la firma y validar los claims (ej. expiración, audiencia).

**Ejemplo de filtro de seguridad en Spring Boot para validación JWT local:**

```java
// src/main/java/com/your_microservice/security/LocalJwtAuthenticationFilter.java
package com.your_microservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LocalJwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}") // La misma clave secreta que usa el Auth Service
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Excluir rutas públicas (ej. Swagger, health checks)
        if (request.getRequestURI().startsWith("/swagger-ui") || request.getRequestURI().startsWith("/v3/api-docs") || request.getRequestURI().startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7); // Eliminar "Bearer "
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 1. Verificar expiración
            if (claims.getExpiration().before(new Date())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token expired");
                return;
            }

            // 2. Extraer username y roles (asumiendo un claim 'roles' separado por comas)
            String username = claims.getSubject();
            List<String> roles = Arrays.asList(claims.get("roles", String.class).split(","));

            // 3. Crear objeto de autenticación de Spring Security
            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            // 4. Establecer la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}
```

**Integración en `SecurityConfig.java` del microservicio:**

```java
// src/main/java/com/your_microservice/security/SecurityConfig.java
package com.your_microservice.security;

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
@EnableMethodSecurity // Habilita @PreAuthorize
public class SecurityConfig {

    private final LocalJwtAuthenticationFilter localJwtAuthenticationFilter;

    public SecurityConfig(LocalJwtAuthenticationFilter localJwtAuthenticationFilter) {
        this.localJwtAuthenticationFilter = localJwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll() // Rutas públicas
                .anyRequest().authenticated() // Todas las demás requieren autenticación
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(localJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Añade tu filtro JWT

        return http.build();
    }
}
```

**Nota:** Para que la validación local funcione, el microservicio debe tener acceso a la misma `jwt.secret` que usa el Auth Service para firmar los tokens.

---

## 6. Recomendaciones generales para cualquier equipo

### Para frontend

- usar `credentials: 'include'` en login y refresh,
- enviar el access token en cada petición protegida,
- limpiar la URL después de recibir un token,
- manejar errores de autenticación de forma clara.

### Para backend

- leer la identidad del usuario desde el header del gateway,
- validar siempre el token en el backend si la ruta es crítica,
- evitar exponer datos sensibles en respuestas a usuarios no autenticados,
- documentar claramente qué rutas son públicas y cuáles protegidas.

### Para seguridad

- usar HTTPS en ambientes reales,
- mantener la secret JWT fuera del código,
- usar tokens de corta duración para access tokens,
- usar refresh tokens con expiración y almacenamiento seguro.

---

## 7. Checklist mínimo de integración

### Frontend

- [ ] Redirige al Login MFE cuando no hay sesión
- [ ] Envía `credentials: 'include'` en login y refresh
- [ ] Envía `Authorization: Bearer <token>` en peticiones protegidas
- [ ] Maneja errores de autenticación y refresh

### Backend

- [ ] Expone endpoints protegidos
- [ ] Lee `X-User-Name` o valida JWT localmente
- [ ] Devuelve `401` para tokens inválidos
- [ ] Configura CORS correctamente

### Gateway

- [ ] Protege las rutas sensibles
- [ ] Valida JWT antes de pasar la petición
- [ ] Propaga la identidad del usuario al microservicio correcto

---

## 8. Conclusión

La integración funciona cuando todos los componentes del ecosistema comparten el mismo contrato:

- el frontend obtiene un token del Auth Service,
- el Gateway valida el token,
- el microservicio usa la identidad recibida para autorizar el acceso.

Si todos los equipos siguen este patrón, la conexión será consistente, segura y fácil de mantener.
