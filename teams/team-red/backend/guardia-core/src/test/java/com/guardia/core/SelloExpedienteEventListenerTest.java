package com.guardia.core;

import com.guardia.core.model.Expediente;
import com.guardia.core.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SelloExpedienteEventListener - Pruebas Unitarias")
class SelloExpedienteEventListenerTest {

    private final SelloExpedienteEventListener listener = new SelloExpedienteEventListener();

    @Test
    @DisplayName("Debe procesar el evento de sellado sin lanzar excepciones (registro de auditoría)")
    void debeProcesarEventoDeSelloSinExcepcion() {
        Expediente expediente = Expediente.builder()
                .folio("EXP-2026-AAAA1111")
                .hashIntegridad("hash-abc")
                .agenteSelladorInfo("{\"username\":\"agomez\"}")
                .build();
        SelloExpedienteEvent event = new SelloExpedienteEvent(this, expediente);

        assertThat(listener).satisfies(l -> l.onSello(event));
    }

    @Test
    @DisplayName("Debe manejar expedientes con campos de auditoría en null sin lanzar excepciones")
    void debeManejarCamposNulos() {
        Expediente expediente = new Expediente();
        SelloExpedienteEvent event = new SelloExpedienteEvent(this, expediente);

        assertThat(listener).satisfies(l -> l.onSello(event));
    }
}
