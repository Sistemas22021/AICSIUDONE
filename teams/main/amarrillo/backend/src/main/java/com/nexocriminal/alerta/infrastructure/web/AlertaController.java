package com.nexocriminal.alerta.infrastructure.web;

import com.nexocriminal.alerta.application.ChangeAlertaEstado;
import com.nexocriminal.alerta.application.ListAlertas;
import com.nexocriminal.alerta.application.ListAlertasPendientes;
import com.nexocriminal.alerta.infrastructure.web.dto.AlertaResponse;
import com.nexocriminal.domain.alerta.EstadoAlerta;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Adapter de entrada REST para alerta (solo lectura + cambio de estado). */
@RestController
@RequestMapping("/api/v1/alertas")
@CrossOrigin(origins = "*")
public class AlertaController {

    private final ListAlertas listAlertas;
    private final ListAlertasPendientes listPendientes;
    private final ChangeAlertaEstado changeEstado;

    public AlertaController(ListAlertas listAlertas, ListAlertasPendientes listPendientes,
                            ChangeAlertaEstado changeEstado) {
        this.listAlertas = listAlertas;
        this.listPendientes = listPendientes;
        this.changeEstado = changeEstado;
    }

    @GetMapping
    public List<AlertaResponse> listar(@RequestParam(required = false) Boolean pendientes) {
        var alertas = Boolean.TRUE.equals(pendientes)
                ? listPendientes.execute()
                : listAlertas.execute();
        return alertas.stream().map(AlertaResponse::new).toList();
    }

    @PatchMapping("/{id}/estado")
    public AlertaResponse cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        EstadoAlerta estado = EstadoAlerta.valueOf(body.get("estado"));
        return new AlertaResponse(changeEstado.execute(id, estado));
    }
}