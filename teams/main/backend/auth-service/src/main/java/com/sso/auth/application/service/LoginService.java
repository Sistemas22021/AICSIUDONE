package com.sso.auth.application.service;

import com.sso.auth.domain.model.RefreshToken;
import com.sso.auth.domain.model.User;
import com.sso.auth.domain.port.in.LoginUseCase;
import com.sso.auth.domain.port.out.RefreshTokenRepository;
import com.sso.auth.domain.port.out.UserRepository;
import com.sso.auth.infrastructure.config.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Caso de uso: Login.
 *
 * <p>Verifica las credenciales, genera el Access Token JWT y el Refresh Token opaco.
 *
 * <p><b>Estrategia de token híbrida:</b>
 * <ul>
 *   <li>{@code accessToken}: JWT firmado, 15 min de vida → el cliente lo guarda en memoria</li>
 *   <li>{@code refreshToken}: UUID opaco → se envía al cliente como HttpOnly Cookie</li>
 * </ul>
 */
@Service
public class LoginService implements LoginUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${sso.auth.refresh-token-expiry-days:7}")
    private int refreshTokenExpiryDays;

    public LoginService(UserRepository userRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        PasswordEncoder passwordEncoder,
                        JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public AuthResult login(LoginCommand command) {
        // 1. Buscar el usuario — mensaje genérico para no revelar si el usuario existe
        User user = userRepository.findByUsername(command.username())
                .orElseThrow(InvalidCredentialsException::new);

        // 2. Verificar contraseña con BCrypt
        boolean passwordMatches = passwordEncoder.matches(command.rawPassword(), user.password());
        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        // 3. Generar Access Token JWT (corto plazo)
        String accessToken = jwtTokenProvider.generateAccessToken(user.username());

        // 4. Generar Refresh Token opaco y persistirlo en BD
        String rawRefreshToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(
                rawRefreshToken,
                user.username(),
                Instant.now().plusSeconds((long) refreshTokenExpiryDays * 24 * 60 * 60)
        );
        refreshTokenRepository.save(refreshToken);

        return new AuthResult(accessToken, rawRefreshToken, user.username());
    }
}
