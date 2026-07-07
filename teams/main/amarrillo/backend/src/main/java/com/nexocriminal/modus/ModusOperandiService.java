package com.nexocriminal.modus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gestiona el catálogo de modus operandi. CRUD + seed inicial.
 */
@Service
@RequiredArgsConstructor
public class ModusOperandiService {

    private final ModusOperandiRepository repository;

    @Transactional(readOnly = true)
    public List<ModusOperandi> listarActivos() {
        return repository.findByActivoTrueOrderByEtiquetaAsc();
    }

    @Transactional(readOnly = true)
    public List<ModusOperandi> listarTodos() {
        return repository.findAll();
    }

    @Transactional
    public ModusOperandi crear(ModusOperandi m) {
        // Normalizar código: mayúsculas, sin espacios
        m.setCodigo(normalizarCodigo(m.getCodigo()));
        if (repository.existsByCodigo(m.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un modus con el código: " + m.getCodigo());
        }
        m.setId(null);
        if (m.getActivo() == null) m.setActivo(true);
        return repository.save(m);
    }

    @Transactional
    public ModusOperandi actualizar(Long id, ModusOperandi datos) {
        ModusOperandi m = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Modus no encontrado: " + id));
        m.setEtiqueta(datos.getEtiqueta());
        m.setDescripcion(datos.getDescripcion());
        if (datos.getActivo() != null) m.setActivo(datos.getActivo());
        // El código no se cambia al editar (lo usa el motor en datos viejos)
        return repository.save(m);
    }

    @Transactional
    public void eliminar(Long id) {
        // Borrado lógico: lo desactivamos para no romper sucesos viejos que lo usan
        ModusOperandi m = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Modus no encontrado: " + id));
        m.setActivo(false);
        repository.save(m);
    }

    private String normalizarCodigo(String codigo) {
        if (codigo == null) return null;
        return codigo.trim().toUpperCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^A-Z0-9_]", "");
    }

    /** Crea una categoría del seed si no existe. */
    @Transactional
    public void sembrarSiNoExiste(String codigo, String etiqueta, String descripcion) {
        if (!repository.existsByCodigo(codigo)) {
            repository.save(ModusOperandi.builder()
                    .codigo(codigo).etiqueta(etiqueta).descripcion(descripcion)
                    .activo(true).build());
        }
    }
}