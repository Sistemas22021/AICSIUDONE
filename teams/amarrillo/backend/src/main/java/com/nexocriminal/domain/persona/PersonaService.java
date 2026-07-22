package com.nexocriminal.domain.persona;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonaService {

    private final PersonaRepository personaRepository;

    public Persona crear(Persona persona) {
        if (personaRepository.findByDocumento(persona.getDocumento()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una persona con ese documento");
        }
        return personaRepository.save(persona);
    }

    @Transactional(readOnly = true)
    public List<Persona> listar() {
        return personaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Persona obtener(Long id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public java.util.Optional<Persona> buscarPorDocumento(String documento) {
        return personaRepository.findByDocumento(documento);
    }

    public Persona actualizar(Long id, Persona datos) {
        Persona existente = obtener(id);
        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setAlias(datos.getAlias());
        existente.setFechaNacimiento(datos.getFechaNacimiento());
        existente.setRol(datos.getRol());
        existente.setTelefono(datos.getTelefono());
        return personaRepository.save(existente);
    }

    public void eliminar(Long id) {
        personaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Persona> buscarIntermediarios(Long victimaId, Long sospechosoId) {
        return personaRepository.encontrarIntermediarios(victimaId, sospechosoId);
    }
}
