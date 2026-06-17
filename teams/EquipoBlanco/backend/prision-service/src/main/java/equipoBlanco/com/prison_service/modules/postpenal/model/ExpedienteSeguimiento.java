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

    private String oficialAsignadoNombre;
    private String oficialAsignadoCedula;

    @ElementCollection
    @CollectionTable(name = "expediente_historial_asignacion", joinColumns = @JoinColumn(name = "expediente_id"))
    @Column(name = "registro")
    @Builder.Default
    private java.util.List<String> historialAsignaciones = new java.util.ArrayList<>();

    public void completarPerfil() {
        this.estado = "completado";
    }

    public void actualizarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
    }
}
