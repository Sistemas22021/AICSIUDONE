package com.guardia.core.service;

import com.guardia.core.model.Escena;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GuardiaTurnoNotificacionService - Pruebas Unitarias")
class GuardiaTurnoNotificacionServiceTest {

    private final GuardiaTurnoNotificacionService service = new GuardiaTurnoNotificacionService();

    @Test
    @DisplayName("Debe notificar sin lanzar excepciones cuando la escena tiene expediente e investigador")
    void debeNotificarConDatosCompletos() {
        Escena escena = Escena.builder()
                .id(1L)
                .expediente(Expediente.builder().folio("EXP-2026-AAAA1111").build())
                .liberadaPor(Usuario.builder().fullName("Carlos Ruiz").build())
                .horaLiberacion(LocalDateTime.now())
                .build();

        assertThat(service).satisfies(s -> s.notificarLiberacionEscena(escena));
    }

    @Test
    @DisplayName("Debe notificar sin lanzar excepciones cuando faltan expediente e investigador")
    void debeNotificarConDatosIncompletos() {
        Escena escena = Escena.builder().id(1L).build();

        assertThat(service).satisfies(s -> s.notificarLiberacionEscena(escena));
    }
}
