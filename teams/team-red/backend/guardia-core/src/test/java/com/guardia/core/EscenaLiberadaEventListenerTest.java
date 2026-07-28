package com.guardia.core;

import com.guardia.core.model.Escena;
import com.guardia.core.service.NotificacionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("EscenaLiberadaEventListener - Pruebas Unitarias")
class EscenaLiberadaEventListenerTest {

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private EscenaLiberadaEventListener listener;

    @Test
    @DisplayName("Debe delegar la notificación de liberación en NotificacionService")
    void debeNotificarLiberacionDeEscena() {
        Escena escena = Escena.builder().id(1L).build();
        EscenaLiberadaEvent event = new EscenaLiberadaEvent(this, escena);

        listener.onEscenaLiberada(event);

        verify(notificacionService).notificarLiberacionEscena(escena);
    }
}
