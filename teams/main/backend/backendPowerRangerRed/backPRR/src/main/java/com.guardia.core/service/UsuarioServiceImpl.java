package com.guardia.core.service;

import com.guardia.core.dto.request.UsuarioRequest;
import com.guardia.core.dto.response.UsuarioResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Usuario;
import com.guardia.core.repository.UsuarioRepository;
import com.guardia.core.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
/**
 * Implementación de UsuarioService que contiene la lógica de negocio para usuarios.
 */
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.existsByIdentificacion(request.identificacion()))
            throw new BusinessException("Ya existe un usuario con esa identificación.");
        if (usuarioRepository.existsByCorreo(request.correo()))
            throw new BusinessException("Ya existe un usuario con ese correo.");

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setIdentificacion(request.identificacion());
        usuario.setCredenciales(request.credenciales());
        usuario.setCorreo(request.correo());

        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPorIdentificacion(String identificacion) {
        return usuarioRepository.findByIdentificacion(identificacion)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con identificación " + identificacion + " no encontrado."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> obtenerTodos() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = findById(id);
        usuario.setNombre(request.nombre());
        usuario.setCorreo(request.correo());
        usuario.setCredenciales(request.credenciales());
        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    public void eliminar(Long id) {
        findById(id);
        usuarioRepository.deleteById(id);
    }

    @Override
    public boolean autenticar(Long id, String credenciales) {
        Usuario usuario = findById(id);
        return usuario.autenticar(credenciales);
    }

    private Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    public UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getIdentificacion(), u.getCorreo());
    }
}
