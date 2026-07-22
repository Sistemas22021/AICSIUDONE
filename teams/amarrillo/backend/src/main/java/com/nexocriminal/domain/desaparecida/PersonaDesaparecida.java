package com.nexocriminal.domain.desaparecida;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nexocriminal.domain.ubicacion.Ubicacion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.nexocriminal.validacion.anotaciones.CedulaVenezolana;
import com.nexocriminal.validacion.anotaciones.TelefonoVenezolano;

@Entity
@Table(name = "persona_desaparecida")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PersonaDesaparecida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @CedulaVenezolana
    @Column(unique = true, nullable = false, length = 50)
    private String documento;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String apellido;

    @Column(length = 100)
    private String alias;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 20)
    private String genero;

    // Datos físicos
    @Column(name = "estatura_cm")
    private Integer estaturaCm;

    @Column(name = "peso_kg")
    private Integer pesoKg;

    @Column(length = 40)
    private String contextura;

    @Column(name = "color_cabello", length = 40)
    private String colorCabello;

    @Column(name = "color_ojos", length = 40)
    private String colorOjos;

    @Column(columnDefinition = "TEXT")
    private String señasParticulares;

    @Column(columnDefinition = "TEXT", name = "ropa_al_desaparecer")
    private String ropaAlDesaparecer;

    // Foto
    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    /**
     * Coleccion de fotos de la persona (multiples fotos).
     * fotoUrl se mantiene como foto principal por compatibilidad.
     */
    @OneToMany(
        mappedBy = "personaDesaparecida",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    @OrderBy("orden ASC")
    @JsonIgnoreProperties({"personaDesaparecida", "hibernateLazyInitializer", "handler"})
    @Builder.Default
    private List<FotoDesaparecida> fotos = new ArrayList<>();

    // Datos de la desaparición
    @NotNull
    @Column(name = "fecha_desaparicion", nullable = false)
    private LocalDateTime fechaDesaparicion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ultima_ubicacion_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Ubicacion ultimaUbicacion;

    @Column(columnDefinition = "TEXT")
    private String circunstancias;

    // Reportante
    @Column(name = "reportante_nombre", length = 150)
    private String reportanteNombre;

    @TelefonoVenezolano
    @Column(name = "reportante_telefono", length = 50)
    private String reportanteTelefono;

    @Column(name = "reportante_relacion", length = 100)
    private String reportanteRelacion;

    // Clasificación
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private EstadoDesaparicion estado = EstadoDesaparicion.BUSCADA;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PrioridadDesaparicion prioridad = PrioridadDesaparicion.MEDIA;

    // Análisis IA
    @Column(name = "analisis_ia", columnDefinition = "TEXT")
    private String analisisIA;

    @Column(name = "zonas_busqueda_ia", columnDefinition = "TEXT")
    private String zonasBusquedaIA;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Column(name = "creado_en", updatable = false)
    @Builder.Default
    private LocalDateTime creadoEn = LocalDateTime.now();

    @Column(name = "actualizado_en")
    @Builder.Default
    private LocalDateTime actualizadoEn = LocalDateTime.now();
}