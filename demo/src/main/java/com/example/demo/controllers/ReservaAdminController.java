package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

import com.example.demo.service.ReservaService;
import com.example.demo.dto.ReservaDTO;

@RestController
@RequestMapping("/v1/admin/reservas")
@Validated
public class ReservaAdminController {

    @Autowired
    private ReservaService reservaService;

    /**
     * GET /v1/admin/reservas
     * Listar todas las reservas con filtros opcionales
     */
    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listarReservas(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String documentoCliente,
            @RequestParam(required = false) UUID vueloId) {
        try {
            List<ReservaDTO> reservas;
            
            if (documentoCliente != null) {
                // Filtro específico por documento del cliente (no ID generado)
                reservas = reservaService.buscarReservasPorClienteId(documentoCliente);
            } else if (estado != null) {
                reservas = reservaService.buscarReservasPorEstado(estado);
            } else if (vueloId != null) {
                reservas = reservaService.buscarReservasPorVuelo(vueloId);
            } else {
                reservas = reservaService.listarTodasReservas();
            }
            
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * GET /v1/admin/reservas/{reservaId}
     * Obtener reserva específica por ID
     */
    @GetMapping("/{reservaId}")
    public ResponseEntity<ReservaDTO> obtenerReserva(@PathVariable String reservaId) {
        try {
            ReservaDTO reserva = reservaService.consultarReserva(reservaId);
            if (reserva != null) {
                return ResponseEntity.ok(reserva);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * GET /v1/admin/reservas/vuelo/{vueloId}
     * Obtener todas las reservas de un vuelo específico
     */
    @GetMapping("/vuelo/{vueloId}")
    public ResponseEntity<List<ReservaDTO>> obtenerReservasPorVuelo(@PathVariable UUID vueloId) {
        try {
            List<ReservaDTO> reservas = reservaService.buscarReservasPorVuelo(vueloId);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * PUT /v1/admin/reservas/{reservaId}/estado
     * Cambiar estado de reserva manualmente (administrativo)
     */
    @PutMapping("/{reservaId}/estado")
    public ResponseEntity<ReservaDTO> cambiarEstadoReserva(
            @PathVariable String reservaId,
            @RequestBody EstadoReservaRequest request) {
        try {
            ReservaDTO reservaActualizada = reservaService.cambiarEstadoAdministrativo(
                reservaId, 
                request.getNuevoEstado(), 
                request.getObservaciones()
            );
            
            if (reservaActualizada != null) {
                return ResponseEntity.ok(reservaActualizada);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * DELETE /v1/admin/reservas/{reservaId}
     * Cancelar reserva administrativamente
     */
    @DeleteMapping("/{reservaId}")
    public ResponseEntity<String> cancelarReservaAdmin(
            @PathVariable String reservaId,
            @RequestParam(required = false, defaultValue = "Cancelación administrativa") String motivo) {
        try {
            ReservaDTO reservaCancelada = reservaService.cancelarPreReserva(reservaId, motivo);
            if (reservaCancelada != null) {
                return ResponseEntity.ok("Reserva cancelada exitosamente");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body("No se puede cancelar: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    // Clase interna para el request de cambio de estado
    public static class EstadoReservaRequest {
        private String nuevoEstado;
        private String observaciones;

        // Getters y setters
        public String getNuevoEstado() {
            return nuevoEstado;
        }

        public void setNuevoEstado(String nuevoEstado) {
            this.nuevoEstado = nuevoEstado;
        }

        public String getObservaciones() {
            return observaciones;
        }

        public void setObservaciones(String observaciones) {
            this.observaciones = observaciones;
        }
    }
}