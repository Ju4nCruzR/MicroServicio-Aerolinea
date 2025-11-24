package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;

import com.example.demo.service.AeropuertoService;
import com.example.demo.dto.AeropuertoDTO;
import com.example.demo.entity.Aeropuerto;

@RestController
@RequestMapping("/v1/admin/aeropuertos")
@Validated
public class AeropuertoController {

    @Autowired
    private AeropuertoService aeropuertoService;

    /**
     * GET /v1/admin/aeropuertos
     * Listar todos los aeropuertos
     */
    @GetMapping
    public ResponseEntity<List<AeropuertoDTO>> listarAeropuertos() {
        try {
            List<AeropuertoDTO> aeropuertos = aeropuertoService.listarTodos();
            return ResponseEntity.ok(aeropuertos);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * GET /v1/admin/aeropuertos/{codigo}
     * Obtener aeropuerto específico por código
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<AeropuertoDTO> obtenerAeropuerto(@PathVariable String codigo) {
        try {
            AeropuertoDTO aeropuerto = aeropuertoService.buscarPorCodigo(codigo);
            if (aeropuerto != null) {
                return ResponseEntity.ok(aeropuerto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * POST /v1/admin/aeropuertos
     * Crear nuevo aeropuerto
     */
    @PostMapping
    public ResponseEntity<AeropuertoDTO> crearAeropuerto(@Valid @RequestBody Aeropuerto aeropuerto) {
        try {
            AeropuertoDTO nuevoAeropuerto = aeropuertoService.crearAeropuerto(aeropuerto);
            return ResponseEntity.status(201).body(nuevoAeropuerto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * PUT /v1/admin/aeropuertos/{codigo}
     * Actualizar aeropuerto existente
     */
    @PutMapping("/{codigo}")
    public ResponseEntity<AeropuertoDTO> actualizarAeropuerto(
            @PathVariable String codigo, 
            @Valid @RequestBody Aeropuerto aeropuerto) {
        try {
            aeropuerto.setCodigoIATA(codigo); // Asegurar que el código coincida
            AeropuertoDTO aeropuertoActualizado = aeropuertoService.actualizarAeropuerto(aeropuerto);
            if (aeropuertoActualizado != null) {
                return ResponseEntity.ok(aeropuertoActualizado);
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
     * DELETE /v1/admin/aeropuertos/{codigo}
     * Eliminar aeropuerto (solo si no tiene vuelos asociados)
     */
    @DeleteMapping("/{codigo}")
    public ResponseEntity<String> eliminarAeropuerto(@PathVariable String codigo) {
        try {
            boolean eliminado = aeropuertoService.eliminarAeropuerto(codigo);
            if (eliminado) {
                return ResponseEntity.ok("Aeropuerto eliminado exitosamente");
            } else {
                return ResponseEntity.status(409).body("No se puede eliminar: aeropuerto tiene vuelos asociados");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }
}