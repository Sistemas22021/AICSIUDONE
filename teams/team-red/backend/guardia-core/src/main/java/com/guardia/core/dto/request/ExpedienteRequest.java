package com.guardia.core.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
/**
 * DTO de solicitud para crear un expediente completo.
 * Incluye ubicacion, lista de delitos, victimas, descripción y denunciante opcional.
 */
public class ExpedienteRequest {

    @NotNull
    private UbicacionRequest ubicacion;

    @NotEmpty(message = "Debe registrar al menos un delito en el expediente")
    private List<DelitoRequest> delitos;

    @NotBlank(message = "debe haber una descripcion")
    private String descripcion;

    private Boolean esDenunciaFormal;

    @NotEmpty(message = "Debe registrar al menos un victima")
    private List<InvolucradosRequest> victimas;

    private InvolucradosRequest denunciante;


    public Boolean getEsDenunciaFormal() {
        return esDenunciaFormal;
    }

    // Compatibility helpers (some code expects record-style accessors)
    public String descripcion() { return this.descripcion; }
    public java.util.List<DelitoRequest> delitos() { return this.delitos; }
    public java.util.List<InvolucradosRequest> victimas() { return this.victimas; }
    public UbicacionRequest ubicacion() { return this.ubicacion; }
    public Boolean esDenunciaFormal() { return this.esDenunciaFormal; }
    public InvolucradosRequest denunciante() { return this.denunciante; }
}
