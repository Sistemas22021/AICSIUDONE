package com.nexocriminal.suceso.infrastructure.persistence;

import com.nexocriminal.domain.persona.PersonaRepository;
import com.nexocriminal.suceso.domain.port.PersonaReaderPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Adapter del puerto de persona para el modulo suceso. */
@Component("sucesoPersonaReaderAdapter")
public class PersonaReaderAdapter implements PersonaReaderPort {

    private final PersonaRepository personaRepository;

    public PersonaReaderAdapter(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @Override
    public Optional<PersonaData> findById(Long id) {
        if (id == null) return Optional.empty();
        return personaRepository.findById(id)
                .map(p -> new PersonaData(p.getId(), p.getNombre(), p.getApellido(), p.getDocumento()));
    }
}