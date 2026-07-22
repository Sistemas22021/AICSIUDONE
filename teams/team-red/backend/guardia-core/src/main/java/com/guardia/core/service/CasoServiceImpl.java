package com.guardia.core.service;

import com.guardia.core.dto.request.CasoRequest;
import com.guardia.core.dto.response.CasoResponse;
import com.guardia.core.dto.response.ExpedienteResumenResponse;
import com.guardia.core.dto.response.UsuarioResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Caso;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.Usuario;
import com.guardia.core.repository.CasoRepository;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CasoServiceImpl implements CasoService {

    private final CasoRepository casoRepository;
    private final ExpedienteRepository expedienteRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public CasoResponse crear(CasoRequest request) {
        Set<Long> idsUnicos = new LinkedHashSet<>(request.expedienteIds());
        if (idsUnicos.size() < 2) {
            throw new BusinessException("Un caso debe agrupar al menos dos expedientes distintos.");
        }

        Usuario creadoPor = usuarioRepository.findByUsername(request.creadoPorUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario con username '" + request.creadoPorUsername() + "' no encontrado."));

        List<Expediente> expedientes = idsUnicos.stream()
                .map(id -> expedienteRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Expediente", id)))
                .toList();

        List<String> yaAgrupados = expedientes.stream()
                .filter(e -> e.getCaso() != null)
                .map(Expediente::getFolio)
                .toList();
        if (!yaAgrupados.isEmpty()) {
            throw new BusinessException(
                    "Los siguientes expedientes ya pertenecen a otro caso y no pueden reasignarse: "
                            + String.join(", ", yaAgrupados));
        }

        Caso caso = Caso.builder()
                .codigoCaso(generarCodigoCaso())
                .motivo(request.motivo())
                .creadoPor(creadoPor)
                .fechaCreacion(LocalDateTime.now())
                .build();
        caso = casoRepository.save(caso);

        for (Expediente expediente : expedientes) {
            expediente.setCaso(caso);
            expedienteRepository.save(expediente);
        }
        caso.setExpedientes(expedientes);

        return toResponse(caso);
    }

    @Override
    @Transactional(readOnly = true)
    public CasoResponse obtenerPorId(Long id) {
        Caso caso = casoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Caso", id));
        return toResponse(caso);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CasoResponse> obtenerTodos() {
        return casoRepository.findAll().stream().map(this::toResponse).toList();
    }

    private String generarCodigoCaso() {
        long siguiente = casoRepository.contarCasos() + 1;
        String candidato = "CASO-%d-%04d".formatted(Year.now().getValue(), siguiente);
        while (casoRepository.existsByCodigoCaso(candidato)) {
            siguiente++;
            candidato = "CASO-%d-%04d".formatted(Year.now().getValue(), siguiente);
        }
        return candidato;
    }

    private CasoResponse toResponse(Caso caso) {
        UsuarioResponse creadoPor = caso.getCreadoPor() == null ? null : new UsuarioResponse(
                caso.getCreadoPor().getId(), caso.getCreadoPor().getUsername(),
                caso.getCreadoPor().getFullName(), caso.getCreadoPor().getProfilePhotoUrl(),  caso.getCreadoPor().getRol());

        List<ExpedienteResumenResponse> expedientes = caso.getExpedientes().stream()
                .map(this::toResumen).toList();

        return new CasoResponse(caso.getId(), caso.getCodigoCaso(), caso.getMotivo(),
                creadoPor, caso.getFechaCreacion(),
                null, // caso.getAlertaOrigen() != null ? caso.getAlertaOrigen().getId() : null
                expedientes);
    }

    private ExpedienteResumenResponse toResumen(Expediente e) {
        return new ExpedienteResumenResponse(
                e.getId(), e.getFolio(),
                e.getTipoDelito() != null ? e.getTipoDelito().getNombre() : null,
                e.getSubtipoDelito() != null ? e.getSubtipoDelito().getNombre() : null,
                e.getEstadoExpediente(), e.getFechaHecho(),
                e.getCreadoPor() != null ? e.getCreadoPor().getFullName() : "Sin asignar",
                e.getLocalizacion() != null ? e.getLocalizacion().getMunicipio() : null);
    }
}