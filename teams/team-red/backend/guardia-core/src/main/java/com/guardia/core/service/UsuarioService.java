package com.guardia.core.service;

import com.guardia.core.dto.request.UsuarioRequest;
import com.guardia.core.dto.response.UsuarioResponse;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestión de usuarios: creación, autenticación y búsquedas.
 */
public interface UsuarioService {
    UsuarioResponse obtenerPorId(UUID  id);
    UsuarioResponse obtenerPorUsername(String username);
    List<UsuarioResponse> obtenerTodos();
    UsuarioResponse actualizar(UUID id, UsuarioRequest request);
    void eliminar(UUID id);
}

