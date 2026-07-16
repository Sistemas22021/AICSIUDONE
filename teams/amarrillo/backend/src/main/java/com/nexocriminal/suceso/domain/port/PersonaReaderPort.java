package com.nexocriminal.suceso.domain.port;

import java.util.Optional;

/** Puerto de lectura de personas: datos minimos de la victima. */
public interface PersonaReaderPort {

    Optional<PersonaData> findById(Long id);

    record PersonaData(Long id, String nombre, String apellido, String documento) {}
}