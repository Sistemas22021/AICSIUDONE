package com.ccc.sistema_balistico.core.infrastructure.in.rest.controller;

import com.ccc.sistema_balistico.core.application.services.AuditLogService;
import com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity.AuditLogViewEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/audit-log")
@Tag(name = "Logs de Auditoría", description = "Endpoints para la consulta paginada de auditoría con Envers")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @Operation(
            summary = "Obtener logs de auditoría paginados",
            description = "Consulta la vista unificada de logs de auditoría generados por Envers con soporte para paginación."
    )
    @GetMapping
    public ResponseEntity<Page<AuditLogViewEntity>> getAuditLogs(
            @PageableDefault() Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getAuditLogs(pageable));
    }
}
