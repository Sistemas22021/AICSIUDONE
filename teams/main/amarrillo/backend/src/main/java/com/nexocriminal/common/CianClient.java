package com.nexocriminal.integracion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Cliente para enviar incidentes al sistema del Equipo Cian (patrullaje).
 * Endpoint de Cian: POST {base}/api/incidents
 */
@Component
@Slf4j
public class CianClient {

    // La URL base de Cian se configura por variable de entorno.
    // Por defecto localhost (para pruebas locales); en despliegue se cambia.
    @Value("${nexo.integracion.cian.url:http://localhost:8080}")
    private String cianBaseUrl;

    private final RestTemplate rest = new RestTemplate();

    /**
     * Envía un incidente a Cian. Devuelve el cuerpo de la respuesta (incluye el id que ellos generan).
     * Lanza excepción si falla, para que el llamador la maneje.
     */
    public Map<String, Object> enviarIncidente(String type, String description,
                                               double latitude, double longitude, String priority) {
        String url = cianBaseUrl + "/api/incidents";

        Map<String, Object> body = Map.of(
                "type", type,
                "description", description != null ? description : "",
                "latitude", latitude,
                "longitude", longitude,
                "priority", priority
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        log.info("Enviando incidente a Cian: {}", url);
        @SuppressWarnings("unchecked")
        ResponseEntity<Map> resp = rest.postForEntity(url, request, Map.class);
        return resp.getBody();
    }
}