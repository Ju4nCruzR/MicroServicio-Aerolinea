package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

import com.example.demo.service.PasajeroService;
import com.example.demo.dto.PasajeroDTO;
import com.example.demo.entity.Pasajero;

@RestController
@RequestMapping("/v1/admin/pasajeros")
@Validated
public class PasajeroController {

    @Autowired
    private PasajeroService pasajeroService;

    /**
     * GET /v1/admin/pasajeros
     * Listar todos los pasajeros con filtros opcionales
     */
    @GetMapping
    public ResponseEntity<List<PasajeroDTO>> listarPasajeros(
            @RequestParam(required = false) String tipoDocumento,
            @RequestParam(required = false) String numeroDocumento,
            @RequestParam(required = false) String nombre) {
        try {
            List<PasajeroDTO> pasajeros = pasajeroService.listarPasajeros(tipoDocumento, numeroDocumento, nombre);
            return ResponseEntity.ok(pasajeros);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * GET /v1/admin/pasajeros/{pasajeroId}
     * Obtener pasajero específico por ID
     */
    @GetMapping("/{pasajeroId}")
    public ResponseEntity<PasajeroDTO> obtenerPasajero(@PathVariable UUID pasajeroId) {
        try {
            PasajeroDTO pasajero = pasajeroService.consultarPasajero(pasajeroId);
            if (pasajero != null) {
                return ResponseEntity.ok(pasajero);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * POST /v1/admin/pasajeros
     * Crear nuevo pasajero
     */
    @PostMapping
    public ResponseEntity<PasajeroDTO> crearPasajero(@Valid @RequestBody Pasajero pasajero) {
        try {
            PasajeroDTO nuevoPasajero = pasajeroService.crearPasajero(pasajero);
            return ResponseEntity.status(201).body(nuevoPasajero);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * PUT /v1/admin/pasajeros/{pasajeroId}
     * Actualizar pasajero existente
     */
    @PutMapping("/{pasajeroId}")
    public ResponseEntity<PasajeroDTO> actualizarPasajero(
            @PathVariable UUID pasajeroId, 
            @Valid @RequestBody Pasajero pasajero) {
        try {
            pasajero.setClientId(pasajeroId.toString()); // Asegurar que el ID coincida
            PasajeroDTO pasajeroActualizado = pasajeroService.actualizarPasajero(pasajero);
            if (pasajeroActualizado != null) {
                return ResponseEntity.ok(pasajeroActualizado);
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
     * DELETE /v1/admin/pasajeros/{pasajeroId}
     * Eliminar pasajero (solo si no tiene reservas activas)
     */
    @DeleteMapping("/{pasajeroId}")
    public ResponseEntity<String> eliminarPasajero(@PathVariable UUID pasajeroId) {
        try {
            boolean eliminado = pasajeroService.eliminarPasajero(pasajeroId);
            if (eliminado) {
                return ResponseEntity.ok("Pasajero eliminado exitosamente");
            } else {
                return ResponseEntity.status(409).body("No se puede eliminar: pasajero tiene reservas activas");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    /**
     * GET /v1/admin/pasajeros/buscar-por-documento
     * Buscar pasajero por tipo y número de documento
     */
    @GetMapping("/buscar-por-documento")
    public ResponseEntity<PasajeroDTO> buscarPorDocumento(
            @RequestParam String tipoDocumento,
            @RequestParam String numeroDocumento) {
        try {
            PasajeroDTO pasajero = pasajeroService.buscarPorDocumento(tipoDocumento, numeroDocumento);
            if (pasajero != null) {
                return ResponseEntity.ok(pasajero);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}