package com.nexocriminal.ia.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatRequest {
    private List<MensajeChat> historial;
    private String pregunta;
    private boolean incluirContexto = true;
}