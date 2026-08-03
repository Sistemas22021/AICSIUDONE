package com.guardia.core.service;

import com.guardia.core.dto.request.EvidenciaRequest;
import com.guardia.core.dto.response.EvidenciaResponse;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Escena;
import com.guardia.core.model.Evidencia;
import com.guardia.core.model.Usuario;
import com.guardia.core.repository.EscenaRepository;
import com.guardia.core.repository.EvidenciaRepository;
import com.guardia.core.repository.UsuarioRepository;
import com.guardia.core.service.EvidenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EvidenciaServiceImpl implements EvidenciaService {

    private final EvidenciaRepository evidenciaRepository;
    private final EscenaRepository escenaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public EvidenciaResponse crear(EvidenciaRequest request) {
        Escena escena = escenaRepository.findById(request.escenaId())
                .orElseThrow(() -> new ResourceNotFoundException("Escena", request.escenaId()));

        Evidencia evidencia = new Evidencia();
        evidencia.setNumeroItem(request.numeroItem());
        evidencia.setTipo(request.tipo());
        evidencia.setDescripcion(request.descripcion());
        evidencia.setEscena(escena);

        return toResponse(evidenciaRepository.save(evidencia));
    }

    @Override
    @Transactional(readOnly = true)
    public EvidenciaResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenciaResponse> obtenerTodos() {
        return evidenciaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenciaResponse> obtenerPorEscena(Long escenaId) {
        return evidenciaRepository.findByEscenaId(escenaId).stream().map(this::toResponse).toList();
    }

    @Override
    public EvidenciaResponse actualizar(Long id, EvidenciaRequest request) {
        Evidencia evidencia = findById(id);
        evidencia.setTipo(request.tipo());
        evidencia.setDescripcion(request.descripcion());
        evidencia.setNumeroItem(request.numeroItem());
        return toResponse(evidenciaRepository.save(evidencia));
    }

    @Override
    public void eliminar(Long id) {
        findById(id);
        evidenciaRepository.deleteById(id);
    }

    @Override
    public EvidenciaResponse asignarNumero(Long id, String numero) {
        Evidencia evidencia = findById(id);
        evidencia.asignarNumero(numero);
        return toResponse(evidenciaRepository.save(evidencia));
    }

    @Override
    public EvidenciaResponse firmarLevantamiento(Long id, Long investigadorId) {
        Evidencia evidencia = findById(id);
        Usuario investigador = usuarioRepository.findById(investigadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", investigadorId));
        evidencia.firmarLevantamiento(investigador);
        return toResponse(evidenciaRepository.save(evidencia));
    }

    @Override
    public boolean validarIntegridad(Long id) {
        return findById(id).validarIntegridad();
    }

    private Evidencia findById(Long id) {
        return evidenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidencia", id));
    }

    public EvidenciaResponse toResponse(Evidencia e) {
        Long escenaId = e.getEscena() != null ? e.getEscena().getId() : null;
        return new EvidenciaResponse(e.getId(), e.getNumeroItem(), e.getTipo(),
                e.getDescripcion(), escenaId);
    }
}
