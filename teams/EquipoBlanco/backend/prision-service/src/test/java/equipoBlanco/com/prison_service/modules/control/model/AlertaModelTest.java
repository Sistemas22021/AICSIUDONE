package equipoBlanco.com.prison_service.modules.control.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class AlertaModelTest {

    @Test
    void testAlertaBuilder() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        
        // Act
        Alerta alerta = Alerta.builder()
                .nivel(1)
                .fechaEmision(now)
                .destinatario("Director")
                .estado("Pendiente")
                .accionRequerida("Revisión de seguridad")
                .build();

        // Assert
        assertNotNull(alerta);
        assertEquals(1, alerta.getNivel());
        assertEquals(now, alerta.getFechaEmision());
        assertEquals("Director", alerta.getDestinatario());
        assertEquals("Pendiente", alerta.getEstado());
        assertEquals("Revisión de seguridad", alerta.getAccionRequerida());
    }
}
