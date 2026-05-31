package com.guardia.core.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Data
public class DelitoRequest {
    @NotBlank(message = "El delito es obligatorio")
    private String delito;

    @NotNull
    private SubDelitoRequest subDelito;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "La fecha del hecho es obligatoria")
    private LocalDate fechaHecho;

    @JsonFormat(pattern = "HH:mm")
    @NotNull
    private LocalTime horaInicioHecho;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFin;

    @NotNull
    private boolean hechoEnCurso;

    // Compatibility accessors (some code expects record-style or getX())
    public String delito() { return this.delito; }
    public SubDelitoRequest subDelito() { return this.subDelito; }
    public LocalDate fechaHecho() { return this.fechaHecho; }
    public LocalTime horaInicioHecho() { return this.horaInicioHecho; }
    public LocalTime horaFin() { return this.horaFin; }
    public Boolean hechoEnCurso() { return this.hechoEnCurso; }

    // Lombok @Data provides getX() methods; add aliases to be safe
    public String getDelito() { return this.delito; }
    public SubDelitoRequest getSubDelito() { return this.subDelito; }
    public LocalDate getFechaHecho() { return this.fechaHecho; }
    public LocalTime getHoraInicioHecho() { return this.horaInicioHecho; }
    public LocalTime getHoraFin() { return this.horaFin; }
    public boolean isHechoEnCurso() { return this.hechoEnCurso; }
}
