package com.nexocriminal.fuentes.adapter;

import com.nexocriminal.fuentes.DatosPersonaExterna;
import com.nexocriminal.fuentes.FuenteDatosPersona;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * PATRON ADAPTER (adaptee concreto).
 *
 * Obtiene datos de una persona haciendo web scraping al portal del CNE,
 * usando la cedula como parametro de busqueda, y traduce el HTML resultante
 * a la estructura comun DatosPersonaExterna.
 *
 * NOTA OPERATIVA: el portal del CNE suele estar geo-restringido o caido.
 * Por eso:
 *   - Toda falla se captura y devuelve "no encontrado" (no rompe el flujo).
 *   - Existe un modo mock (nexo.fuentes.cne.mock=true) para demos.
 *
 * IMPORTANTE: los selectores HTML del parseo deben verificarse contra la
 * estructura real del portal al momento de usarlo, ya que el CNE puede
 * cambiar el formato de su pagina.
 */
@Component
@Slf4j
public class CneScrapingAdapter implements FuenteDatosPersona {

    @Value("${nexo.fuentes.cne.url:http://www.cne.gob.ve/web/registro_electoral/ce.php}")
    private String urlBase;

    @Value("${nexo.fuentes.cne.timeout-ms:8000}")
    private int timeoutMs;

    @Value("${nexo.fuentes.cne.mock:false}")
    private boolean mockHabilitado;

    @Override
    public String nombre() {
        return "CNE";
    }

    @Override
    public DatosPersonaExterna buscarPorCedula(String cedula) {
        if (mockHabilitado) {
            return mock(cedula);
        }

        try {
            String nacionalidad = extraerNacionalidad(cedula); // V o E
            String numero = extraerNumero(cedula);             // solo digitos

            log.info("[CNE] Consultando cedula {}-{}", nacionalidad, numero);

            Document doc = Jsoup.connect(urlBase)
                    .data("nacionalidad", nacionalidad)
                    .data("cedula", numero)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(timeoutMs)
                    .get();

            return parsear(doc, cedula);

        } catch (Exception e) {
            // Cualquier falla (timeout, geo-bloqueo, HTML cambiado) NO rompe el sistema
            log.warn("[CNE] No se pudo consultar la cedula {}: {}", cedula, e.getMessage());
            return DatosPersonaExterna.noEncontrado(cedula, nombre());
        }
    }

    /**
     * Parsea el HTML de respuesta del CNE.
     * TODO: verificar y ajustar los selectores contra la estructura real
     * del portal. La pagina del CNE devuelve los datos dentro de una tabla;
     * aqui se hace una lectura defensiva del texto.
     */
    private DatosPersonaExterna parsear(Document doc, String cedula) {
        String texto = doc.text();

        // El portal devuelve "No existe..." o similar si no encuentra la cedula
        if (texto.toLowerCase().contains("no existe")
                || texto.toLowerCase().contains("no se encontr")) {
            return DatosPersonaExterna.noEncontrado(cedula, nombre());
        }

        // Lectura defensiva: se buscan las celdas de la tabla de resultados.
        // Estos selectores deben ajustarse al HTML real del CNE.
        var celdas = doc.select("table td");
        if (celdas.isEmpty()) {
            return DatosPersonaExterna.noEncontrado(cedula, nombre());
        }

        String nombreCompleto = celdas.size() > 1 ? celdas.get(1).text() : null;
        String estado = celdas.size() > 2 ? celdas.get(2).text() : null;
        String municipio = celdas.size() > 3 ? celdas.get(3).text() : null;
        String parroquia = celdas.size() > 4 ? celdas.get(4).text() : null;
        String centro = celdas.size() > 5 ? celdas.get(5).text() : null;

        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            return DatosPersonaExterna.noEncontrado(cedula, nombre());
        }

        return DatosPersonaExterna.builder()
                .cedula(cedula)
                .nombreCompleto(nombreCompleto.trim())
                .estado(estado)
                .municipio(municipio)
                .parroquia(parroquia)
                .centroVotacion(centro)
                .fuente(nombre())
                .encontrado(true)
                .build();
    }

    private String extraerNacionalidad(String cedula) {
        if (cedula == null) return "V";
        String c = cedula.trim().toUpperCase();
        if (c.startsWith("E")) return "E";
        return "V";
    }

    private String extraerNumero(String cedula) {
        if (cedula == null) return "";
        return cedula.replaceAll("[^0-9]", "");
    }

    /** Datos simulados para demo cuando el CNE no es accesible. */
    private DatosPersonaExterna mock(String cedula) {
        log.info("[CNE-MOCK] Generando datos simulados para {}", cedula);
        return DatosPersonaExterna.builder()
                .cedula(cedula)
                .nombreCompleto("JUAN ANTONIO PEREZ GONZALEZ")
                .primerNombre("JUAN")
                .segundoNombre("ANTONIO")
                .primerApellido("PEREZ")
                .segundoApellido("GONZALEZ")
                .estado("NUEVA ESPARTA")
                .municipio("MARIÑO")
                .parroquia("SANTIAGO MARIÑO")
                .centroVotacion("U.E. NACIONAL FRANCISCO ESTEBAN GOMEZ")
                .fuente(nombre() + " (MOCK)")
                .encontrado(true)
                .build();
    }
}