package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

import com.example.demo.service.VueloService;
import com.example.demo.dto.VueloDTO;
import com.example.demo.entity.Vuelo;

@RestController
@RequestMapping("/v1/admin/vuelos")
@Validated
public class VueloAdminController {

    @Autowired
    private VueloService vueloService;

    /**
     * GET /v1/admin/vuelos
     * Listar todos los vuelos con filtros opcionales
     */
    @GetMapping
    public ResponseEntity<List<VueloDTO>> listarVuelos(
            @RequestParam(required = false) String origen,
            @RequestParam(required = false) String destino,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String aerolinea) {
        try {
            List<VueloDTO> vuelos = vueloService.listarVuelosAdmin(origen, destino, estado, aerolinea);
            return ResponseEntity.ok(vuelos);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * GET /v1/admin/vuelos/{vueloId}
     * Obtener vuelo específico con detalles administrativos
     */
    @GetMapping("/{vueloId}")
    public ResponseEntity<VueloDTO> obtenerVueloDetallado(@PathVariable UUID vueloId) {
        try {
            VueloDTO vuelo = vueloService.consultarVuelo(vueloId);
            if (vuelo != null) {
                return ResponseEntity.ok(vuelo);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * POST /v1/admin/vuelos
     * Crear nuevo vuelo (Administrativo)
     */
    @PostMapping
    public ResponseEntity<VueloDTO> crearVuelo(@Valid @RequestBody Vuelo vuelo) {
        try {
            VueloDTO nuevoVuelo = vueloService.crearVuelo(vuelo);
            return ResponseEntity.status(201).body(nuevoVuelo);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * PUT /v1/admin/vuelos/{vueloId}
     * Actualizar vuelo existente
     */
    @PutMapping("/{vueloId}")
    public ResponseEntity<VueloDTO> actualizarVuelo(
            @PathVariable UUID vueloId, 
            @Valid @RequestBody Vuelo vuelo) {
        try {
            vuelo.setVueloId(vueloId); // Asegurar que el ID coincida
            VueloDTO vueloActualizado = vueloService.actualizarVuelo(vuelo);
            if (vueloActualizado != null) {
                return ResponseEntity.ok(vueloActualizado);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * DELETE /v1/admin/vuelos/{vueloId}
     * Cancelar/eliminar vuelo (solo si no tiene reservas confirmadas)
     */
    @DeleteMapping("/{vueloId}")
    public ResponseEntity<String> eliminarVuelo(@PathVariable UUID vueloId) {
        try {
            boolean eliminado = vueloService.eliminarVuelo(vueloId);
            if (eliminado) {
                return ResponseEntity.ok("Vuelo eliminado exitosamente");
            } else {
                return ResponseEntity.status(409).body("No se puede eliminar: vuelo tiene reservas confirmadas");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    /**
     * PUT /v1/admin/vuelos/{vueloId}/disponibilidad
     * Ajustar disponibilidad del vuelo manualmente
     */
    @PutMapping("/{vueloId}/disponibilidad")
    public ResponseEntity<VueloDTO> ajustarDisponibilidad(
            @PathVariable UUID vueloId,
            @RequestBody DisponibilidadRequest request) {
        try {
            VueloDTO vueloActualizado = vueloService.ajustarDisponibilidad(
                vueloId, 
                request.getNuevaDisponibilidad(),
                request.getMotivo()
            );
            
            if (vueloActualizado != null) {
                return ResponseEntity.ok(vueloActualizado);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * GET /v1/admin/vuelos/{vueloId}/reservas
     * Ver todas las reservas de un vuelo específico
     */
    @GetMapping("/{vueloId}/reservas")
    public ResponseEntity<com.example.demo.dto.ResumenReservasVueloDTO> obtenerReservasVuelo(@PathVariable UUID vueloId) {
        try {
            com.example.demo.dto.ResumenReservasVueloDTO resumen = vueloService.obtenerResumenReservas(vueloId);
            if (resumen != null) {
                return ResponseEntity.ok(resumen);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * PUT /v1/admin/vuelos/{vueloId}/estado
     * Cambiar estado del vuelo (PROGRAMADO, EN_VUELO, COMPLETADO, CANCELADO)
     */
    @PutMapping("/{vueloId}/estado")
    public ResponseEntity<VueloDTO> cambiarEstadoVuelo(
            @PathVariable UUID vueloId,
            @RequestBody EstadoVueloRequest request) {
        try {
            VueloDTO vueloActualizado = vueloService.cambiarEstadoVuelo(
                vueloId, 
                request.getNuevoEstado(),
                request.getObservaciones()
            );
            
            if (vueloActualizado != null) {
                return ResponseEntity.ok(vueloActualizado);
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

    // Clases internas para requests
    public static class DisponibilidadRequest {
        private Integer nuevaDisponibilidad;
        private String motivo;

        public Integer getNuevaDisponibilidad() {
            return nuevaDisponibilidad;
        }

        public void setNuevaDisponibilidad(Integer nuevaDisponibilidad) {
            this.nuevaDisponibilidad = nuevaDisponibilidad;
        }

        public String getMotivo() {
            return motivo;
        }

        public void setMotivo(String motivo) {
            this.motivo = motivo;
        }
    }

    public static class EstadoVueloRequest {
        private String nuevoEstado;
        private String observaciones;

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