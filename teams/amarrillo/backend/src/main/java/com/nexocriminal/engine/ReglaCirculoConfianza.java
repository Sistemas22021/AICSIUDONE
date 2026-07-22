package com.nexocriminal.engine;

import com.nexocriminal.domain.alerta.Alerta;
import com.nexocriminal.domain.alerta.NivelRiesgo;
import com.nexocriminal.domain.alerta.TipoAlerta;
import com.nexocriminal.domain.persona.Persona;
import com.nexocriminal.domain.persona.PersonaRepository;
import com.nexocriminal.domain.persona.RolPersona;
import com.nexocriminal.domain.suceso.SucesoRepository;
import com.nexocriminal.domain.suceso.TipoSuceso;
import com.nexocriminal.domain.vinculo.Vinculo;
import com.nexocriminal.domain.vinculo.VinculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * CU-C: Análisis del Círculo de Confianza.
 *
 * Lógica (MVP):
 *   1. Tomar todas las personas con rol VICTIMA asociadas a DESAPARICION.
 *   2. Para cada víctima, iterar sobre personas con rol SOSPECHOSO.
 *   3. Usar la consulta recursiva del repositorio para buscar intermediarios.
 *   4. Si existe intermediario, crear vínculo + alerta.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReglaCirculoConfianza implements ReglaVinculo {

    private final PersonaRepository personaRepository;
    private final SucesoRepository sucesoRepository;
    private final VinculoRepository vinculoRepository;
    private final JpaRepository<Alerta, Long> alertaRepository;

    @Override
    public String nombre() {
        return "REGLA_CIRCULO_CONFIANZA";
    }

    @Override
    public ResultadoRegla ejecutar() {
        log.info("Ejecutando {}", nombre());

        List<Vinculo> vinculos = new ArrayList<>();
        List<Alerta> alertas = new ArrayList<>();

        // Víctimas que aparecen en casos de desaparición
        List<Persona> victimas = sucesoRepository.findByTipo(TipoSuceso.DESAPARICION)
                .stream()
                .map(s -> s.getVictima())
                .filter(v -> v != null)
                .distinct()
                .toList();

        List<Persona> sospechosos = personaRepository.findByRol(RolPersona.SOSPECHOSO);

        for (Persona victima : victimas) {
            for (Persona sospechoso : sospechosos) {
                List<Persona> intermediarios = personaRepository
                        .encontrarIntermediarios(victima.getId(), sospechoso.getId());

                for (Persona inter : intermediarios) {
                    if (inter.getId().equals(victima.getId())
                            || inter.getId().equals(sospechoso.getId())) continue;

                    if (!vinculoRepository.findDuplicado("PERSONA", victima.getId(),
                            "PERSONA", inter.getId(), nombre()).isEmpty()) continue;

                    Vinculo v1 = Vinculo.builder()
                            .tipoOrigen("PERSONA").idOrigen(victima.getId())
                            .tipoDestino("PERSONA").idDestino(inter.getId())
                            .reglaDetectada(nombre())
                            .score(BigDecimal.valueOf(0.85))
                            .build();
                    vinculos.add(vinculoRepository.save(v1));

                    Vinculo v2 = Vinculo.builder()
                            .tipoOrigen("PERSONA").idOrigen(inter.getId())
                            .tipoDestino("PERSONA").idDestino(sospechoso.getId())
                            .reglaDetectada(nombre())
                            .score(BigDecimal.valueOf(0.85))
                            .build();
                    vinculos.add(vinculoRepository.save(v2));

                    Alerta a = Alerta.builder()
                            .tipo(TipoAlerta.INTERMEDIARIO)
                            .titulo("Intermediario critico: " +
                                    inter.getNombre() + " " + inter.getApellido())
                            .descripcion(String.format(
                                    "La victima %s %s y el sospechoso %s %s comparten a %s %s como contacto comun.",
                                    victima.getNombre(), victima.getApellido(),
                                    sospechoso.getNombre(), sospechoso.getApellido(),
                                    inter.getNombre(), inter.getApellido()))
                            .nivelRiesgo(NivelRiesgo.ALTO)
                            .build();
                    alertas.add(alertaRepository.save(a));
                }
            }
        }

        log.info("{} completada: {} vinculos, {} alertas",
                nombre(), vinculos.size(), alertas.size());
        return new ResultadoRegla(nombre(), vinculos, alertas);
    }
}
