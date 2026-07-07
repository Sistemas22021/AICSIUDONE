package com.nexocriminal.vehiculo.infrastructure.persistence;

import com.nexocriminal.domain.persona.PersonaRepository;
import com.nexocriminal.vehiculo.domain.port.PersonaReaderPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter del puerto de lectura de personas. Usa el PersonaRepository JPA
 * existente para devolver solo los datos minimos del propietario.
 */
@Component
public class PersonaReaderAdapter implements PersonaReaderPort {

    private final PersonaRepository personaRepository;

    public PersonaReaderAdapter(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @Override
    public Optional<PropietarioData> findById(Long id) {
        if (id == null) return Optional.empty();
        return personaRepository.findById(id)
                .map(p -> new PropietarioData(p.getId(), p.getNombre(), p.getApellido(), p.getDocumento()));
    }
}