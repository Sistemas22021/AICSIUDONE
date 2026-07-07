package com.nexocriminal.ia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexocriminal.ia.dto.MensajeChat;
import com.nexocriminal.ia.dto.RespuestaIA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP para la API de Anthropic (Claude).
 */
@Component
@Slf4j
public class ClaudeClient {

    @Value("${nexo.ia.api-key:}")
    private String apiKey;

    @Value("${nexo.ia.modelo:claude-sonnet-4-5}")
    private String modelo;

    @Value("${nexo.ia.max-tokens:2048}")
    private int maxTokens;

    @Value("${nexo.ia.url:https://api.anthropic.com/v1/messages}")
    private String url;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    public boolean configurada() {
        return apiKey != null && !apiKey.isBlank();
    }

    /**
     * Envía un prompt simple y obtiene la respuesta.
     */
    public RespuestaIA preguntar(String systemPrompt, String userPrompt) {
        return preguntar(systemPrompt, List.of(new MensajeChat("user", userPrompt)));
    }

    /**
     * Envía una conversación con historial y obtiene respuesta.
     */
    public RespuestaIA preguntar(String systemPrompt, List<MensajeChat> mensajes) {
        if (!configurada()) {
            throw new IllegalStateException(
                "API Key de Anthropic no configurada. Definí la variable ANTHROPIC_API_KEY o el property nexo.ia.api-key"
            );
        }

        long inicio = System.currentTimeMillis();

        try {
            // Construir body
            Map<String, Object> body = new HashMap<>();
            body.put("model", modelo);
            body.put("max_tokens", maxTokens);
            if (systemPrompt != null && !systemPrompt.isBlank()) {
                body.put("system", systemPrompt);
            }
            body.put("messages", mensajes);

            String jsonBody = mapper.writeValueAsString(body);

            log.debug("Enviando a Claude: {} mensajes, modelo={}", mensajes.size(), modelo);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Error de Claude API ({}): {}", response.statusCode(), response.body());
                throw new RuntimeException("Error en API de Claude: HTTP " + response.statusCode() +
                        " - " + response.body());
            }

            JsonNode root = mapper.readTree(response.body());

            // Extraer contenido
            StringBuilder contenido = new StringBuilder();
            JsonNode contentArray = root.get("content");
            if (contentArray != null && contentArray.isArray()) {
                for (JsonNode item : contentArray) {
                    if ("text".equals(item.path("type").asText())) {
                        contenido.append(item.path("text").asText());
                    }
                }
            }

            // Stats
            JsonNode usage = root.get("usage");
            int inputTokens = usage != null ? usage.path("input_tokens").asInt(0) : 0;
            int outputTokens = usage != null ? usage.path("output_tokens").asInt(0) : 0;

            long fin = System.currentTimeMillis();

            return RespuestaIA.builder()
                    .contenido(contenido.toString())
                    .modelo(modelo)
                    .tokensEntrada(inputTokens)
                    .tokensSalida(outputTokens)
                    .durationMs(fin - inicio)
                    .build();

        } catch (Exception e) {
            log.error("Error al consultar Claude: {}", e.getMessage(), e);
            throw new RuntimeException("Error al consultar IA: " + e.getMessage(), e);
        }
    }
}