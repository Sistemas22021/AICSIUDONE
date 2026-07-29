package com.nexocriminal.domain.desaparecida;

import com.nexocriminal.files.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonaDesaparecidaService {

    private final PersonaDesaparecidaRepository repository;
    private final FotoDesaparecidaRepository fotoRepository;
    private final FileStorageService fileStorageService;

    public PersonaDesaparecida crear(PersonaDesaparecida p) {
        p.setCreadoEn(LocalDateTime.now());
        p.setActualizadoEn(LocalDateTime.now());
        return repository.save(p);
    }

    public PersonaDesaparecida actualizar(Long id, PersonaDesaparecida datos) {
        PersonaDesaparecida existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Persona desaparecida no encontrada: " + id));
        datos.setId(id);
        datos.setCreadoEn(existente.getCreadoEn());
        datos.setActualizadoEn(LocalDateTime.now());
        if (datos.getEstado() != EstadoDesaparicion.BUSCADA && existente.getFechaResolucion() == null) {
            datos.setFechaResolucion(LocalDateTime.now());
        }
        return repository.save(datos);
    }

    public PersonaDesaparecida cambiarEstado(Long id, EstadoDesaparicion nuevoEstado) {
        PersonaDesaparecida p = obtener(id);
        p.setEstado(nuevoEstado);
        p.setActualizadoEn(LocalDateTime.now());
        if (nuevoEstado != EstadoDesaparicion.BUSCADA && p.getFechaResolucion() == null) {
            p.setFechaResolucion(LocalDateTime.now());
        }
        return repository.save(p);
    }

    public PersonaDesaparecida actualizarFotoUrl(Long id, String url) {
        PersonaDesaparecida p = obtener(id);
        p.setFotoUrl(url);
        p.setActualizadoEn(LocalDateTime.now());
        return repository.save(p);
    }

    public PersonaDesaparecida guardarAnalisisIA(Long id, String analisis) {
        PersonaDesaparecida p = obtener(id);
        p.setAnalisisIA(analisis);
        p.setActualizadoEn(LocalDateTime.now());
        return repository.save(p);
    }

    public PersonaDesaparecida guardarZonasBusquedaIA(Long id, String zonas) {
        PersonaDesaparecida p = obtener(id);
        p.setZonasBusquedaIA(zonas);
        p.setActualizadoEn(LocalDateTime.now());
        return repository.save(p);
    }

    @Transactional(readOnly = true)
    public List<PersonaDesaparecida> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PersonaDesaparecida> listarPorEstado(EstadoDesaparicion estado) {
        return repository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public List<PersonaDesaparecida> listarPorPrioridad(PrioridadDesaparicion prioridad) {
        return repository.findByPrioridad(prioridad);
    }

    @Transactional(readOnly = true)
    public PersonaDesaparecida obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Persona desaparecida no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public List<PersonaDesaparecida> buscarEnRadio(double lat, double lng, int radioMetros) {
        return repository.findEnRadio(lat, lng, radioMetros);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    // ==========================================================
    //  Multiples fotos
    // ==========================================================

    @Transactional
    public FotoDesaparecida agregarFoto(Long personaId, MultipartFile archivo) {
        PersonaDesaparecida persona = obtener(personaId);
        String url = fileStorageService.guardarFotoDesaparecida(archivo);

        long cantidadActual = fotoRepository.countByPersonaDesaparecidaId(personaId);

        // Marcar esta foto como principal SOLO si la persona no tiene ya una foto
        // principal. Ojo: la foto principal puede vivir en dos lugares distintos:
        //   1. Como registro principal=true en la tabla foto_desaparecida, o
        //   2. Como el campo foto_url de la persona (subido con "subir foto principal",
        //      que no crea registro en la galeria).
        // Si NO contemplaramos el caso 2, al subir la primera foto a la galeria se
        // marcaria principal y pisaria la foto_url existente, borrando la principal.
        boolean tienePrincipalEnGaleria = fotoRepository
                .findByPersonaDesaparecidaIdOrderByOrdenAsc(personaId)
                .stream()
                .anyMatch(f -> Boolean.TRUE.equals(f.getPrincipal()));
        boolean tieneFotoUrl = persona.getFotoUrl() != null && !persona.getFotoUrl().isBlank();
        boolean yaTienePrincipal = tienePrincipalEnGaleria || tieneFotoUrl;

        boolean esPrincipal = !yaTienePrincipal;

        FotoDesaparecida foto = FotoDesaparecida.builder()
                .url(url)
                .orden((int) cantidadActual)
                .principal(esPrincipal)
                .personaDesaparecida(persona)
                .build();
        foto = fotoRepository.save(foto);

        // Solo si esta foto pasa a ser la principal, sincronizamos el campo foto_url.
        // Si la persona ya tenia principal, NO tocamos foto_url: se preserva.
        if (esPrincipal) {
            persona.setFotoUrl(url);
            persona.setActualizadoEn(LocalDateTime.now());
            repository.save(persona);
        }

        return foto;
    }

    @Transactional(readOnly = true)
    public List<FotoDesaparecida> listarFotos(Long personaId) {
        return fotoRepository.findByPersonaDesaparecidaIdOrderByOrdenAsc(personaId);
    }

    @Transactional
    public void eliminarFoto(Long personaId, Long fotoId) {
        PersonaDesaparecida persona = obtener(personaId);
        FotoDesaparecida foto = persona.getFotos().stream()
                .filter(f -> f.getId().equals(fotoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Foto no encontrada"));

        boolean eraPrincipal = Boolean.TRUE.equals(foto.getPrincipal());
        String urlEliminada = foto.getUrl();

        // Quitar de la coleccion: orphanRemoval=true la elimina de la BD.
        // NO usar fotoRepository.delete() aca, porque el cascade la re-insertaria.
        persona.getFotos().remove(foto);

        // Si era la principal, designar una nueva a partir de las restantes
        if (eraPrincipal) {
            if (!persona.getFotos().isEmpty()) {
                FotoDesaparecida nuevaPrincipal = persona.getFotos().get(0);
                nuevaPrincipal.setPrincipal(true);
                persona.setFotoUrl(nuevaPrincipal.getUrl());
            } else {
                persona.setFotoUrl(null);
            }
        }

        persona.setActualizadoEn(LocalDateTime.now());
        repository.save(persona);

        // Borrar el archivo fisico despues de confirmar el cambio en BD
        fileStorageService.eliminarArchivo(urlEliminada);
    }

    @Transactional
    public void marcarPrincipal(Long personaId, Long fotoId) {
        List<FotoDesaparecida> fotos = fotoRepository
                .findByPersonaDesaparecidaIdOrderByOrdenAsc(personaId);
        PersonaDesaparecida persona = obtener(personaId);
        for (FotoDesaparecida f : fotos) {
            boolean esLaElegida = f.getId().equals(fotoId);
            f.setPrincipal(esLaElegida);
            if (esLaElegida) {
                persona.setFotoUrl(f.getUrl());
            }
        }
        fotoRepository.saveAll(fotos);
        persona.setActualizadoEn(LocalDateTime.now());
        repository.save(persona);
    }
}