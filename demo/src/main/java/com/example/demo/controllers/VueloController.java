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

import com.example.demo.service.VueloService;
import com.example.demo.service.ReservaService;
import com.example.demo.service.PasajeroService;
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
    
    @Autowired
    private PasajeroService pasajeroService;

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
     * POST /v1/vuelos/reservas/confirmar
     * Confirmar o denegar pre-reserva
     */
    @PostMapping("/reservas/confirmar")
    public ResponseEntity<ConfirmacionResponseDTO> confirmarReserva(@Valid @RequestBody ConfirmacionRequestDTO request) {
        try {
            ReservaDTO reservaConfirmada = reservaService.confirmarODenegarReserva(
                request.getReservaVueloId(),
                request.getTransaccionId(),
                request.getPrecioTotalConfirmado(),
                request.getEstado()
            );
            
            // Crear respuesta específica
            ConfirmacionResponseDTO response = new ConfirmacionResponseDTO();
            response.setReservaConfirmadaId(reservaConfirmada.getReservaConfirmadaId());
            response.setEstadoFinal(reservaConfirmada.getEstado());
            response.setPrecioTotalConfirmado(reservaConfirmada.getPrecioTotal());
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ConfirmacionResponseDTO errorResponse = new ConfirmacionResponseDTO();
            errorResponse.setReservaConfirmadaId(null);
            errorResponse.setEstadoFinal("ERROR");
            errorResponse.setPrecioTotalConfirmado(0.0);
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (IllegalStateException e) {
            ConfirmacionResponseDTO errorResponse = new ConfirmacionResponseDTO();
            errorResponse.setReservaConfirmadaId(null);
            errorResponse.setEstadoFinal("CONFLICTO");
            errorResponse.setPrecioTotalConfirmado(0.0);
            return ResponseEntity.status(409).body(errorResponse);
            
        } catch (Exception e) {
            ConfirmacionResponseDTO errorResponse = new ConfirmacionResponseDTO();
            errorResponse.setReservaConfirmadaId(null);
            errorResponse.setEstadoFinal("ERROR");
            errorResponse.setPrecioTotalConfirmado(0.0);
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
     * POST /v1/vuelos/pasajeros/asignar
     * Asignar asiento a pasajero (check-in)
     */
    @PostMapping("/pasajeros/asignar")
    public ResponseEntity<AsignacionPasajeroResponseDTO> asignarPasajero(@Valid @RequestBody AsignacionPasajeroRequestDTO request) {
        try {
            boolean asignado = pasajeroService.asignarAsientoAPasajero(
                request.getFlightId(),
                request.getClientId(),
                request.getReservationId(),
                request.getAsiento()
            );
            
            AsignacionPasajeroResponseDTO response = new AsignacionPasajeroResponseDTO();
            response.setAsignacionId(java.util.UUID.randomUUID().toString());
            response.setEstado(asignado ? "ASIGNADO" : "ERROR");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            AsignacionPasajeroResponseDTO errorResponse = new AsignacionPasajeroResponseDTO();
            errorResponse.setAsignacionId(null);
            errorResponse.setEstado("ERROR");
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (IllegalStateException e) {
            AsignacionPasajeroResponseDTO errorResponse = new AsignacionPasajeroResponseDTO();
            errorResponse.setAsignacionId(null);
            errorResponse.setEstado("CONFLICTO");
            return ResponseEntity.status(409).body(errorResponse);
            
        } catch (Exception e) {
            AsignacionPasajeroResponseDTO errorResponse = new AsignacionPasajeroResponseDTO();
            errorResponse.setAsignacionId(null);
            errorResponse.setEstado("ERROR");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * GET /v1/vuelos/{vueloId}
     * Consultar vuelo específico (Administrativo - JWT)
     */
    @GetMapping("/{vueloId}")
    public ResponseEntity<VueloDTO> obtenerVuelo(@PathVariable String vueloId) {
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
