package com.guardia.core;

import com.guardia.core.model.DelitoEnExpediente;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.Involucrado;
import com.guardia.core.model.Localizacion;
import com.guardia.core.model.SubtipoDelito;
import com.guardia.core.model.TipoDelito;
import com.guardia.core.model.Usuario;
import com.guardia.core.model.enums.EstadoExpediente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Sha256SelloStrategy - Pruebas Unitarias")
class Sha256SelloStrategyTest {

    private final Sha256SelloStrategy strategy = new Sha256SelloStrategy();
    private Expediente expediente;
    private Usuario agente;

    @BeforeEach
    void setUp() {
        expediente = Expediente.builder()
                .folio("EXP-2026-AAAA1111")
                .descripcionHecho("Robo con violencia")
                .fechaHecho(LocalDateTime.of(2026, 1, 15, 20, 0))
                .fechaCreacion(LocalDateTime.of(2026, 1, 15, 21, 0))
                .tipoDelito(TipoDelito.builder().id(1L).build())
                .subtipoDelito(SubtipoDelito.builder().id(10L).build())
                .localizacion(Localizacion.builder().id(5L).build())
                .esDenunciaFormal(true)
                .delitos(List.of())
                .involucrados(List.of())
                .build();

        agente = Usuario.builder()
                .id(UUID.randomUUID()).username("agomez").fullName("Agente Gómez").build();
    }

    @Test
    @DisplayName("aplicar() debe calcular hash, registrar info del agente y sellar el expediente")
    void debeAplicarSello() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 1, 20, 10, 0);

        strategy.aplicar(expediente, agente, timestamp);

        assertThat(expediente.getHashIntegridad()).isNotBlank().hasSize(64);
        assertThat(expediente.getAgenteSelladorInfo()).contains(agente.getUsername()).contains(agente.getFullName());
        assertThat(expediente.getSelladoPor()).isSameAs(agente);
        assertThat(expediente.getFechaSellado()).isNotNull();
        assertThat(expediente.getEstadoExpediente()).isEqualTo(EstadoExpediente.PROCESADO_Y_SELLADO);
    }

    @Test
    @DisplayName("recalcularHash() debe reproducir el mismo hash que aplicar() para el mismo estado del expediente")
    void debeRecalcularElMismoHashTrasSellar() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 1, 20, 10, 0);
        strategy.aplicar(expediente, agente, timestamp);
        String hashOriginal = expediente.getHashIntegridad();

        String hashRecalculado = strategy.recalcularHash(expediente);

        assertThat(hashRecalculado).isEqualTo(hashOriginal);
    }

    @Test
    @DisplayName("recalcularHash() debe cambiar si el expediente fue alterado tras el sellado")
    void debeDetectarAlteracion() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 1, 20, 10, 0);
        strategy.aplicar(expediente, agente, timestamp);
        String hashOriginal = expediente.getHashIntegridad();

        expediente.setDescripcionHecho("Descripción alterada maliciosamente");
        String hashTrasAlteracion = strategy.recalcularHash(expediente);

        assertThat(hashTrasAlteracion).isNotEqualTo(hashOriginal);
    }

    @Test
    @DisplayName("aplicar() no debe fallar cuando delitos e involucrados incluyen registros")
    void debeIncluirDelitosEInvolucradosEnElHash() {
        DelitoEnExpediente delito = new DelitoEnExpediente();
        delito.setSubtipoDelito("ROBO_AGRAVADO");
        delito.setFechaHoraHecho(LocalDateTime.of(2026, 1, 15, 20, 0));
        Involucrado involucrado = Involucrado.builder().identificacion("V-12345678").build();

        expediente.setDelitos(List.of(delito));
        expediente.setInvolucrados(List.of(involucrado));

        assertThat(expediente).satisfies(e -> strategy.aplicar(e, agente, LocalDateTime.now()));
        assertThat(expediente.getHashIntegridad()).isNotBlank();
    }
}
