package com.nexocriminal.ia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeChat {
    private String role;     // "user" o "assistant"
    private String content;
}