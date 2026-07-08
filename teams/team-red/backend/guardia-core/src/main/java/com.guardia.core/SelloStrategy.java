package com.guardia.core;

import com.guardia.core.model.Expediente;
import com.guardia.core.model.Usuario;
import java.time.LocalDateTime;

public interface SelloStrategy {
    //Aplica el sello al expediente: calcula hash, registra agente y timestamp.
    void aplicar(Expediente expediente, Usuario agente, LocalDateTime timestamp);

    //Recalcula el hash del estado actual del expediente para comparación.
    String recalcularHash(Expediente expediente);
}