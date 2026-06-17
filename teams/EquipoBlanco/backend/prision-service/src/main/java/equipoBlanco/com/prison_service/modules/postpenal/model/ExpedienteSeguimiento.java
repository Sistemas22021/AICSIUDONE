package equipoBlanco.com.prison_service.modules.postpenal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "expediente_seguimiento")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExpedienteSeguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID idRecluso;

    private LocalDate fechaEgreso;
    private String domicilio;
    private String municipio;
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
    private String nivelRiesgo;
    private String estado;

    public void completarPerfil() {
        this.estado = "completado";
    }

    public void actualizarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
    }
}
