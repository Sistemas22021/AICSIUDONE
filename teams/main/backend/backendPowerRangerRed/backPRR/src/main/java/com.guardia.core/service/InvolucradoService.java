package com.guardia.core.service;

import com.guardia.core.dto.request.InvolucradosRequest;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.Involucrado;
import com.guardia.core.repository.ExpedienteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvolucradoService {

    private final ExpedienteRepository expedienteRepository;

    // Inyección por constructor
    public InvolucradoService(ExpedienteRepository expedienteRepository) {
        this.expedienteRepository = expedienteRepository;
    }

    @Transactional
    public void añadirInvolucradoAExpediente(Long expedienteId, InvolucradosRequest request) {
        // 1. Buscar el expediente en la base de datos
        Expediente expediente = expedienteRepository.findById(expedienteId)
                .orElseThrow(() -> new EntityNotFoundException("Expediente con ID " + expedienteId + " no encontrado"));

        // 2. Mapear el Request (DTO) a la Entidad Involucrado
        Involucrado nuevoInvolucrado = Involucrado.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .identificacion(request.getCedula())
                .telefono(request.getTelefono())
                .nacionalidad(request.getNacionalidad())
                .direccion(request.getDireccion())
                .fotografiaURL(request.getFotografiaURL())
                .roles(request.getRol()) // Recuerda cambiar "Rol" a minúscula "rol" en el DTO si puedes
                .build();

        // 3. Vincularlo al expediente usando el método helper de la entidad
        expediente.vincularInvolucrado(nuevoInvolucrado);

        // 4. Guardar el expediente
        // Gracias a CascadeType.ALL, Hibernate detectará el nuevo involucrado en la lista
        // e insertará la nueva fila en la tabla 'expediente_involucrados' con su respectivo 'expediente_id'.
        expedienteRepository.save(expediente);
    }
}