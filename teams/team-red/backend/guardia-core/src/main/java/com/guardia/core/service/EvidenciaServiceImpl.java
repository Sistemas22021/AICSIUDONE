package com.guardia.core.service;
import com.guardia.core.HashStrategy;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
/**
 * Implementación de EvidenciaService: lógica para persistencia y verificación de evidencias.
 */
public class EvidenciaServiceImpl implements EvidenciaService {

    private final EvidenciaRepository evidenciaRepository;
    private final EscenaRepository escenaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HashStrategy hashStrategy;

    @Override
    public EvidenciaResponse crear(EvidenciaRequest request) {
        Escena escena = escenaRepository.findByIdWithInvestigador(request.escenaId())
                .orElseThrow(() -> new ResourceNotFoundException("Escena", request.escenaId()));

        long total = evidenciaRepository.countByEscenaId(request.escenaId());
        String numeroItem = String.format("EV-%03d", total + 1);

        Usuario investigador;
        if (request.investigadorId() != null) {
            investigador = usuarioRepository.findById(request.investigadorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario", request.investigadorId()));
        } else {
            investigador = escena.getLevantadaPor() != null
                    ? usuarioRepository.findById(escena.getLevantadaPor().getId()).orElse(null)
                    : null;
        }

        Evidencia evidencia = new Evidencia();
        evidencia.registrarEvidencia(
                escena,
                request.tipo(),
                request.descripcion(),
                investigador,
                hashStrategy,
                request.hashArchivoCliente()
        );
        evidencia.asignarNumero(numeroItem);

        return toResponse(evidenciaRepository.save(evidencia));
    }

    public boolean verificarHash(Long id) {
        Evidencia evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidencia", id));
        return evidencia.verificarHash(hashStrategy);
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
    public EvidenciaResponse firmarLevantamiento(Long id, UUID investigadorId) {
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

    private EvidenciaResponse toResponse(Evidencia e) {
        String investigadorNombre = e.getInvestigador() != null
                ? e.getInvestigador().getFullName() : null;
        return new EvidenciaResponse(
                e.getId(),
                e.getNumeroItem(),
                e.getTipo(),
                e.getDescripcion(),
                e.getEscena() != null ? e.getEscena().getId() : null,
                e.getHashIntegridad(),
                e.getTimestampRegistro(),
                investigadorNombre
        );
    }
}
