/**
 * Implementación de UsuarioService que contiene la lógica de negocio para usuarios.
 */

package com.guardia.core.service;

import com.guardia.core.dto.request.UsuarioRequest;
import com.guardia.core.dto.response.UsuarioResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Usuario;
import com.guardia.core.repository.UsuarioRepository;
import com.guardia.core.security.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional

public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(UUID id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario con username '" + username + "' no encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> obtenerTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UsuarioResponse actualizar(UUID id, UsuarioRequest request) {
        Usuario usuario = findById(id);

        if (request.fullName() != null && !request.fullName().isBlank()) {
            usuario.setFullName(request.fullName());
        }

        if (request.profilePhotoUrl() != null) {
            usuario.setProfilePhotoUrl(request.profilePhotoUrl());
        }

        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    public void eliminar(UUID id) {
        Usuario usuario = findById(id);
        usuarioRepository.deleteById(id);
    }


    private Usuario findById(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getFullName(),
                usuario.getProfilePhotoUrl()
        );
    }
}