package com.azulcian.GestionIncidentesPatrullas.config.security;

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
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_NAME = "X-User-Name";
    private static final String HEADER_USER_ROLE = "X-User-Role";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String username = request.getHeader(HEADER_USER_NAME);
        String role = request.getHeader(HEADER_USER_ROLE);

        if (username != null && !username.trim().isEmpty() && role != null && !role.trim().isEmpty()) {
            String formattedRole = role.startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase();
            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(formattedRole));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
