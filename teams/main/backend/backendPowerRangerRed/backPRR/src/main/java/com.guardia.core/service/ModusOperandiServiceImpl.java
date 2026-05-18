package com.guardia.core.service;

import com.guardia.core.dto.request.ModusOperandiRequest;
import com.guardia.core.dto.response.ModusOperandiResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.ModusOperandi;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.ModusOperandiRepository;
import com.guardia.core.service.ModusOperandiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ModusOperandiServiceImpl implements ModusOperandiService {

    private final ModusOperandiRepository modusOperandiRepository;
    private final ExpedienteRepository expedienteRepository;

    @Override
    public ModusOperandiResponse crear(ModusOperandiRequest request) {
        ModusOperandi modus = ModusOperandi.builder()
                .descripcionAnalitica(request.descripcionAnalitica())
                .patronDetectado(request.patronDetectado())
                .nivelConfianza(request.nivelConfianza())
                .expedientes(new ArrayList<>())
                .build();
        return toResponse(modusOperandiRepository.save(modus));
    }

    @Override
    @Transactional(readOnly = true)
    public ModusOperandiResponse obtenerPorId(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModusOperandiResponse> obtenerTodos() {
        return modusOperandiRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModusOperandiResponse> buscarPorPatron(String patron) {
        return modusOperandiRepository.findByPatronDetectadoContainingIgnoreCase(patron)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public ModusOperandiResponse actualizar(Long id, ModusOperandiRequest request) {
        ModusOperandi modus = findById(id);
        modus.setDescripcionAnalitica(request.descripcionAnalitica());
        modus.setPatronDetectado(request.patronDetectado());
        modus.setNivelConfianza(request.nivelConfianza());
        return toResponse(modusOperandiRepository.save(modus));
    }

    @Override
    public void eliminar(Long id) {
        findById(id);
        modusOperandiRepository.deleteById(id);
    }

    @Override
    public ModusOperandiResponse vincularExpediente(Long modusId, Long expedienteId) {
        ModusOperandi modus = findById(modusId);
        Expediente expediente = expedienteRepository.findById(expedienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Expediente", expedienteId));

        boolean yaVinculado = modus.getExpedientes().stream()
                .anyMatch(e -> e.getId().equals(expedienteId));
        if (yaVinculado)
            throw new BusinessException("El expediente ya está vinculado a este modus operandi.");

        modus.getExpedientes().add(expediente);
        expediente.getModusOperandiList().add(modus);
        return toResponse(modusOperandiRepository.save(modus));
    }

    @Override
    public ModusOperandiResponse desvincularExpediente(Long modusId, Long expedienteId) {
        ModusOperandi modus = findById(modusId);
        modus.getExpedientes().removeIf(e -> e.getId().equals(expedienteId));
        return toResponse(modusOperandiRepository.save(modus));
    }

    @Override
    public ModusOperandiResponse agregarPatron(Long id, String patron) {
        ModusOperandi modus = findById(id);
        modus.agregarPatron(patron);
        return toResponse(modusOperandiRepository.save(modus));
    }

    @Override
    public double compararExpedientes(Long modusId, Long expedienteAId, Long expedienteBId) {
        ModusOperandi modus = findById(modusId);
        Expediente a = expedienteRepository.findById(expedienteAId)
                .orElseThrow(() -> new ResourceNotFoundException("Expediente", expedienteAId));
        Expediente b = expedienteRepository.findById(expedienteBId)
                .orElseThrow(() -> new ResourceNotFoundException("Expediente", expedienteBId));
        return modus.compararExpedientes(a, b);
    }

    private ModusOperandi findById(Long id) {
        return modusOperandiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ModusOperandi", id));
    }

    public ModusOperandiResponse toResponse(ModusOperandi m) {
        List<Long> expedienteIds = m.getExpedientes() == null ? List.of() :
                m.getExpedientes().stream().map(Expediente::getId).toList();
        return new ModusOperandiResponse(m.getId(), m.getDescripcionAnalitica(),
                m.getPatronDetectado(), m.getNivelConfianza(), expedienteIds);
    }
}
