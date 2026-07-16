package com.guardia.core;

import com.guardia.core.model.Expediente;
import com.guardia.core.model.Usuario;
import com.guardia.core.model.enums.EstadoExpediente;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Component
public class Sha256SelloStrategy implements SelloStrategy {

    @Override
    public void aplicar(Expediente expediente, Usuario agente, LocalDateTime timestamp) {
        String hash = construirHash(expediente, timestamp);
        String infoAgente = String.format(
                "{\"id\":%d,\"nombre\":\"%s\",\"identificacion\":\"%s\",\"timestamp\":\"%s\"}",
                agente.getId(), agente.getNombre(), agente.getIdentificacion(), timestamp
        );
        expediente.setHashIntegridad(hash);
        expediente.setAgenteSelladorInfo(infoAgente);
        expediente.setFechaSellado(timestamp.truncatedTo(java.time.temporal.ChronoUnit.MILLIS));
        expediente.setSelladoPor(agente);
        expediente.setEstadoExpediente(EstadoExpediente.PROCESADO_Y_SELLADO);
    }

    @Override
    public String recalcularHash(Expediente expediente) {
        return construirHash(expediente, expediente.getFechaSellado());
    }

    private String construirHash(Expediente e, LocalDateTime ts) {
        StringBuilder sb = new StringBuilder();
        sb.append(safe(e.getFolio())).append("|")
                .append(safe(e.getDescripcionHecho())).append("|")
                .append(safe(e.getFechaHecho())).append("|")
                .append(safe(e.getFechaCreacion())).append("|")
                .append(ts != null ? ts.truncatedTo(java.time.temporal.ChronoUnit.MILLIS).toString() : "").append("|")
                .append(e.getTipoDelito()    != null ? e.getTipoDelito().getId()    : "").append("|")
                .append(e.getSubtipoDelito() != null ? e.getSubtipoDelito().getId() : "").append("|")
                .append(e.getLocalizacion()  != null ? e.getLocalizacion().getId()  : "").append("|")
                .append(safe(e.getEsDenunciaFormal()));

        if (e.getDelitos() != null)
            e.getDelitos().forEach(d -> sb.append("|D:").append(safe(d.getSubtipoDelito()))
                    .append(":").append(safe(d.getFechaHoraHecho())));
        if (e.getInvolucrados() != null)
            e.getInvolucrados().forEach(v -> sb.append("|V:").append(safe(v.getIdentificacion())));

        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256")
                    .digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("SHA-256 no disponible", ex);
        }
    }

    private String safe(Object o) { return o == null ? "" : o.toString(); }
}