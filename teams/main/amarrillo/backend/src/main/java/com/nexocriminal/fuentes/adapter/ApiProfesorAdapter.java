package com.nexocriminal.fuentes.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexocriminal.fuentes.DatosPersonaExterna;
import com.nexocriminal.fuentes.FuenteDatosPersona;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * PATRON ADAPTER (adaptee concreto).
 *
 * Consume la API externa provista por el profesor y traduce su respuesta
 * JSON a la estructura comun DatosPersonaExterna.
 *
 * NOTA: la estructura del JSON de respuesta aun no esta definida (la API
 * la entregara el profesor). El parseo se hace de forma DEFENSIVA buscando
 * varios nombres de campo posibles. Cuando se conozca el formato real, solo
 * hay que ajustar el metodo parsear().
 */
@Component
@Slf4j
public class ApiProfesorAdapter implements FuenteDatosPersona {

    @Value("${nexo.fuentes.api-profesor.url:}")
    private String urlBase;

    @Value("${nexo.fuentes.api-profesor.timeout-ms:8000}")
    private int timeoutMs;

    @Value("${nexo.fuentes.api-profesor.api-key:}")
    private String apiKey;

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    @Override
    public String nombre() {
        return "API_PROFESOR";
    }

    @Override
    public DatosPersonaExterna buscarPorCedula(String cedula) {
        if (urlBase == null || urlBase.isBlank()) {
            log.warn("[API_PROFESOR] URL no configurada (nexo.fuentes.api-profesor.url)");
            return DatosPersonaExterna.noEncontrado(cedula, nombre());
        }

        try {
            String numero = cedula.replaceAll("[^0-9]", "");
            String url = urlBase.endsWith("/") ? urlBase + numero : urlBase + "/" + numero;

            log.info("[API_PROFESOR] Consultando {}", url);

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(timeoutMs))
                    .header("Accept", "application/json")
                    .GET();

            if (apiKey != null && !apiKey.isBlank()) {
                builder.header("Authorization", "Bearer " + apiKey);
            }

            HttpResponse<String> resp = http.send(builder.build(),
                    HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200) {
                log.warn("[API_PROFESOR] HTTP {} al consultar cedula {}", resp.statusCode(), cedula);
                return DatosPersonaExterna.noEncontrado(cedula, nombre());
            }

            return parsear(resp.body(), cedula);

        } catch (Exception e) {
            log.warn("[API_PROFESOR] Falla al consultar cedula {}: {}", cedula, e.getMessage());
            return DatosPersonaExterna.noEncontrado(cedula, nombre());
        }
    }

    /**
     * Parsea el JSON de la API del profesor.
     * Lectura DEFENSIVA: busca varios nombres de campo posibles.
     * Ajustar cuando se conozca la estructura real de la respuesta.
     */
    private DatosPersonaExterna parsear(String json, String cedula) {
        try {
            JsonNode root = mapper.readTree(json);

            // Si la API envuelve los datos en "data" o "resultado", entrar ahi
            JsonNode n = root.has("data") ? root.get("data")
                    : root.has("resultado") ? root.get("resultado")
                    : root;

            String nombreCompleto = texto(n, "nombreCompleto", "nombre_completo", "nombre", "fullName");
            if (nombreCompleto == null) {
                return DatosPersonaExterna.noEncontrado(cedula, nombre());
            }

            return DatosPersonaExterna.builder()
                    .cedula(cedula)
                    .nombreCompleto(nombreCompleto)
                    .primerNombre(texto(n, "primerNombre", "primer_nombre"))
                    .primerApellido(texto(n, "primerApellido", "primer_apellido", "apellido"))
                    .estado(texto(n, "estado", "state"))
                    .municipio(texto(n, "municipio"))
                    .parroquia(texto(n, "parroquia"))
                    .fuente(nombre())
                    .encontrado(true)
                    .build();

        } catch (Exception e) {
            log.warn("[API_PROFESOR] No se pudo parsear la respuesta: {}", e.getMessage());
            return DatosPersonaExterna.noEncontrado(cedula, nombre());
        }
    }

    /** Busca el primer campo que exista entre varios nombres posibles. */
    private String texto(JsonNode n, String... posiblesNombres) {
        for (String nombre : posiblesNombres) {
            if (n.has(nombre) && !n.get(nombre).isNull()) {
                String v = n.get(nombre).asText();
                if (v != null && !v.isBlank()) return v.trim();
            }
        }
        return null;
    }
}