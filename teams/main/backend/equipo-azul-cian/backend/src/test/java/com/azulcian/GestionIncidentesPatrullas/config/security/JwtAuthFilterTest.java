package com.azulcian.GestionIncidentesPatrullas.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthFilterTest {

    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        jwtAuthFilter = new JwtAuthFilter();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithHeaders_SetsAuthentication() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Name", "mando_juan");
        request.addHeader("X-User-Role", "CENTRO_MANDO");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(auth);
        assertEquals("mando_juan", auth.getName());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CENTRO_MANDO")));

        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithoutHeaders_UsesDefaultSupervisorAuthentication()
            throws ServletException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = Mockito.mock(FilterChain.class);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(auth);
        assertEquals("super_user", auth.getName());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERVISOR")));

        Mockito.verify(filterChain).doFilter(request, response);
    }
}
