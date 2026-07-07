package com.nexocriminal.persona.infrastructure.web;

import com.nexocriminal.persona.application.*;
import com.nexocriminal.persona.domain.model.Persona;
import com.nexocriminal.persona.infrastructure.web.dto.PersonaRequest;
import com.nexocriminal.persona.infrastructure.web.dto.PersonaResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Adapter de entrada REST para persona. */
@RestController
@RequestMapping("/api/v1/personas")
@CrossOrigin(origins = "*")
public class PersonaController {

    private final CreatePersona createPersona;
    private final ListPersonas listPersonas;
    private final GetPersona getPersona;
    private final UpdatePersona updatePersona;
    private final DeletePersona deletePersona;
    private final FindIntermediarios findIntermediarios;

    public PersonaController(CreatePersona createPersona, ListPersonas listPersonas,
                             GetPersona getPersona, UpdatePersona updatePersona,
                             DeletePersona deletePersona, FindIntermediarios findIntermediarios) {
        this.createPersona = createPersona;
        this.listPersonas = listPersonas;
        this.getPersona = getPersona;
        this.updatePersona = updatePersona;
        this.deletePersona = deletePersona;
        this.findIntermediarios = findIntermediarios;
    }

    @GetMapping
    public List<PersonaResponse> listar() {
        return listPersonas.execute().stream().map(PersonaResponse::new).toList();
    }

    @GetMapping("/{id}")
    public PersonaResponse obtener(@PathVariable Long id) {
        return new PersonaResponse(getPersona.execute(id));
    }

    @PostMapping
    public ResponseEntity<PersonaResponse> crear(@Valid @RequestBody PersonaRequest req) {
        Persona nueva = Persona.crear(
                req.getDocumento(), req.getNombre(), req.getApellido(), req.getAlias(),
                req.getFechaNacimiento(), req.getRol(), req.getTelefono());
        return ResponseEntity.ok(new PersonaResponse(createPersona.execute(nueva)));
    }

    @PutMapping("/{id}")
    public PersonaResponse actualizar(@PathVariable Long id, @Valid @RequestBody PersonaRequest req) {
        Persona p = updatePersona.execute(
                id, req.getNombre(), req.getApellido(), req.getAlias(),
                req.getFechaNacimiento(), req.getRol(), req.getTelefono());
        return new PersonaResponse(p);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        deletePersona.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/intermediarios")
    public List<PersonaResponse> intermediarios(@RequestParam Long victimaId,
                                                @RequestParam Long sospechosoId) {
        return findIntermediarios.execute(victimaId, sospechosoId)
                .stream().map(PersonaResponse::new).toList();
    }
}