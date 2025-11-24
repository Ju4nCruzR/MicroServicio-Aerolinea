package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

import com.example.demo.service.VueloService;
import com.example.demo.service.ReservaService;
import com.example.demo.dto.*;
import com.example.demo.entity.Vuelo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/vuelos")
@Validated
public class VueloController {

    @Autowired
    private VueloService vueloService;
    
    @Autowired
    private ReservaService reservaService;

    /**
     * POST /v1/vuelos/buscar
     * Busca vuelos disponibles según criterios del ecosistema de turismo
     */
    @PostMapping("/buscar")
    public ResponseEntity<BusquedaVuelosResponseDTO> buscarVuelos(@Valid @RequestBody BusquedaVuelosRequestDTO request) {
        try {
            // Validar fechas futuras
            if (request.getFechaSalida().isBefore(java.time.LocalDate.now())) {
                BusquedaVuelosResponseDTO errorResponse = new BusquedaVuelosResponseDTO();
                errorResponse.setConsultaId(java.util.UUID.randomUUID().toString());
                errorResponse.setVuelos(new java.util.ArrayList<>());
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Buscar vuelos usando el servicio
            List<VueloDTO> vuelos = vueloService.buscarVuelos(
                request.getOrigen(),
                request.getDestino(), 
                request.getNumPasajeros(),
                request.getFechaSalida(),
                request.getFechaRegreso(),
                request.getClase()
            );
            
            // Crear respuesta
            BusquedaVuelosResponseDTO response = new BusquedaVuelosResponseDTO();
            response.setConsultaId(java.util.UUID.randomUUID().toString());
            response.setVuelos(vuelos);
            
            if (vuelos.isEmpty()) {
                return ResponseEntity.status(404).body(response);
            }

            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            BusquedaVuelosResponseDTO errorResponse = new BusquedaVuelosResponseDTO();
            errorResponse.setConsultaId(null);
            errorResponse.setVuelos(new java.util.ArrayList<>());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            BusquedaVuelosResponseDTO errorResponse = new BusquedaVuelosResponseDTO();
            errorResponse.setConsultaId(null);
            errorResponse.setVuelos(new java.util.ArrayList<>());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * POST /v1/vuelos/reservar
     * Crear pre-reserva de vuelo
     */
    @PostMapping("/reservar")
    public ResponseEntity<PreReservaResponseDTO> reservarVuelo(@Valid @RequestBody PreReservaRequestDTO request) {
        try {
            // Crear pre-reserva usando el servicio
            ReservaDTO reservaCreada = reservaService.crearPreReserva(
                request.getVueloId(),
                request.getNumPasajeros(),
                request.getContactoReserva(),
                request.getDocumentoContacto()
            );
            
            // Crear respuesta específica
            PreReservaResponseDTO response = new PreReservaResponseDTO();
            response.setReservaVueloId(reservaCreada.getReservaVueloId());
            response.setEstadoInicial(reservaCreada.getEstado());
            response.setFechaExpiracion(reservaCreada.getFechaExpiracion());
            response.setPrecioTotal(reservaCreada.getPrecioTotal());
            response.setObservaciones("Pre-reserva creada exitosamente");
            
            return ResponseEntity.status(201).body(response);
            
        } catch (IllegalArgumentException e) {
            PreReservaResponseDTO errorResponse = new PreReservaResponseDTO();
            errorResponse.setReservaVueloId(null);
            errorResponse.setEstadoInicial("ERROR");
            errorResponse.setObservaciones("Error de validación: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            PreReservaResponseDTO errorResponse = new PreReservaResponseDTO();
            errorResponse.setReservaVueloId(null);
            errorResponse.setEstadoInicial("ERROR");
            errorResponse.setObservaciones("Error interno del servidor");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }



    /**
     * PUT /v1/vuelos/reservas/{reservaId}/confirmar
     * Confirmar reserva después del procesamiento bancario
     */
    @PutMapping("/reservas/{reservaId}/confirmar")
    public ResponseEntity<ConfirmacionReservaResponseDTO> confirmarReserva(
            @PathVariable String reservaId, 
            @Valid @RequestBody ConfirmacionReservaRequestDTO request) {
        try {
            // Confirmar reserva usando el servicio
            ReservaDTO reservaConfirmada = reservaService.confirmarReserva(
                reservaId,
                request.getTransaccionBancariaId(),
                request.getMetodoPago()
            );
            
            // Crear respuesta específica
            ConfirmacionReservaResponseDTO response = new ConfirmacionReservaResponseDTO();
            response.setReservaVueloId(reservaConfirmada.getReservaVueloId());
            response.setPnr(reservaConfirmada.getReservaConfirmadaId()); // PNR está en este campo
            response.setEstadoFinal(reservaConfirmada.getEstado());
            response.setTransaccionBancariaId(reservaConfirmada.getTransaccionId()); // TransaccionId es el campo correcto
            response.setObservaciones("Reserva confirmada exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ConfirmacionReservaResponseDTO errorResponse = new ConfirmacionReservaResponseDTO();
            errorResponse.setReservaVueloId(reservaId);
            errorResponse.setEstadoFinal("ERROR");
            errorResponse.setObservaciones("Error de validación: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (IllegalStateException e) {
            ConfirmacionReservaResponseDTO errorResponse = new ConfirmacionReservaResponseDTO();
            errorResponse.setReservaVueloId(reservaId);
            errorResponse.setEstadoFinal("ERROR");
            errorResponse.setObservaciones("Estado inválido: " + e.getMessage());
            return ResponseEntity.status(409).body(errorResponse);
            
        } catch (Exception e) {
            ConfirmacionReservaResponseDTO errorResponse = new ConfirmacionReservaResponseDTO();
            errorResponse.setReservaVueloId(reservaId);
            errorResponse.setEstadoFinal("ERROR");
            errorResponse.setObservaciones("Error interno del servidor");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * DELETE /v1/vuelos/reservas/{reservaId}
     * Cancelar pre-reserva
     */
    @DeleteMapping("/reservas/{reservaId}")
    public ResponseEntity<String> cancelarReserva(@PathVariable String reservaId) {
        try {
            ReservaDTO reservaCancelada = reservaService.cancelarPreReserva(reservaId, "Cancelación manual");
            if (reservaCancelada != null) {
                return ResponseEntity.ok("Pre-reserva cancelada exitosamente");
            } else {
                return ResponseEntity.status(404).body("Reserva no encontrada");
            }
            
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body("No se puede cancelar: " + e.getMessage());
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    /**
     * GET /v1/vuelos/{vueloId}
     * Consultar vuelo específico (Administrativo - JWT)
     */
    @GetMapping("/{vueloId}")
    public ResponseEntity<VueloDTO> obtenerVuelo(@PathVariable UUID vueloId) {
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
     * POST /v1/vuelos
     * Crear nuevo vuelo (Administrativo - JWT)
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
    

}
