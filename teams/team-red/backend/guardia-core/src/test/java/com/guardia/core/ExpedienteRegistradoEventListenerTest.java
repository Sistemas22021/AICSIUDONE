package com.guardia.core;

import com.guardia.core.service.DeteccionModusOperandiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExpedienteRegistradoEventListener - Pruebas Unitarias")
class ExpedienteRegistradoEventListenerTest {

    @Mock
    private DeteccionModusOperandiService deteccionModusOperandiService;

    @InjectMocks
    private ExpedienteRegistradoEventListener listener;

    @Test
    @DisplayName("Debe disparar el análisis de Modus Operandi para el expediente del evento")
    void debeDispararAnalisisDeModusOperandi() {
        ExpedienteRegistradoEvent event = new ExpedienteRegistradoEvent(this, 42L);

        listener.onExpedienteRegistrado(event);

        verify(deteccionModusOperandiService).analizarPatrones(42L);
    }
}
