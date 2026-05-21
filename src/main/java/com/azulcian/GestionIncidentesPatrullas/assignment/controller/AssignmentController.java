package com.azulcian.GestionIncidentesPatrullas.assignment.controller;

import com.azulcian.GestionIncidentesPatrullas.assignment.dto.AssignmentRequestDTO;
import com.azulcian.GestionIncidentesPatrullas.assignment.model.Assignment;
import com.azulcian.GestionIncidentesPatrullas.assignment.service.AssignmentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    // =========================================
    // ASIGNAR PATRULLA A INCIDENTE (CORE DEL SISTEMA)
    // =========================================
    @PostMapping
    public Assignment assignPatrol(@RequestBody AssignmentRequestDTO dto) {
        return assignmentService.assign(dto);
    }
}