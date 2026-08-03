package com.guardia.core.service;

import com.guardia.core.dto.request.UsuarioRequest;
import com.guardia.core.dto.response.UsuarioResponse;

import java.util.List;

public interface UsuarioService {
    UsuarioResponse crear(UsuarioRequest request);
    UsuarioResponse obtenerPorId(Long id);
    UsuarioResponse obtenerPorIdentificacion(String identificacion);
    List<UsuarioResponse> obtenerTodos();
    UsuarioResponse actualizar(Long id, UsuarioRequest request);
    void eliminar(Long id);
    boolean autenticar(Long id, String credenciales);
}
