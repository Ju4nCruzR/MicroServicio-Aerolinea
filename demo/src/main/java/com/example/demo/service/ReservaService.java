package com.example.demo.service;

import com.example.demo.entity.Reserva;
import com.example.demo.entity.Vuelo;
import com.example.demo.dto.ReservaDTO;
import com.example.demo.mapper.ReservaMapper;
import com.example.demo.repository.ReservaRepository;
import com.example.demo.repository.VueloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.UUID;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private ReservaMapper reservaMapper;
    
    // Para manejo de timers de expiración
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final Map<String, ScheduledFuture<?>> expirationTasks = new ConcurrentHashMap<>();

    /**
     * Crear pre-reserva según especificaciones del ecosistema turístico
     * POST /v1/vuelos/reservar
     */
    public ReservaDTO crearPreReserva(String vueloId, Integer numPasajeros, String contactoReserva, String documentoContacto) {
        // Convertir string a UUID
        UUID vueloUUID;
        try {
            vueloUUID = UUID.fromString(vueloId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("VueloId debe ser un UUID válido");
        }
        
        // Validar que el vuelo exista y esté en estado PROGRAMADO
        Vuelo vuelo = vueloRepository.findById(vueloUUID)
            .orElseThrow(() -> new IllegalArgumentException("Vuelo no encontrado"));
            
        if (!"PROGRAMADO".equals(vuelo.getEstado())) {
            throw new IllegalArgumentException("El vuelo no está disponible para reservas");
        }
        
        // Validar que la fecha del vuelo sea futura
        if (vuelo.getFechaSalida().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se pueden hacer reservas para vuelos pasados");
        }
        
        // Verificar disponibilidad suficiente (sin gestión de asientos individuales)
        if (vuelo.getDisponibilidad() < numPasajeros) {
            throw new IllegalStateException("No hay suficientes asientos disponibles");
        }
        
        // Crear la reserva en estado PENDIENTE (esperando confirmación del banco)
        Reserva reserva = new Reserva();
        reserva.setReservaVueloId(generarReservaVueloId());
        reserva.setVuelo(vuelo);
        reserva.setNumPasajeros(numPasajeros);
        reserva.setContactoReserva(contactoReserva);
        reserva.setDocumentoContacto(documentoContacto);
        reserva.setPrecioTotal(vuelo.getPrecio() * numPasajeros);
        reserva.setEstado("PENDIENTE"); // Estado inicial pendiente
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setFechaExpiracion(LocalDateTime.now().plusMinutes(30)); // 30 minutos para confirmar
        reserva.setObservaciones("Pre-reserva creada - esperando confirmación bancaria");
        
        // BLOQUEAR disponibilidad temporalmente (sin asientos específicos)
        vuelo.setDisponibilidad(vuelo.getDisponibilidad() - numPasajeros);
        vueloRepository.save(vuelo);
        
        // Programar auto-cancelación si no se confirma en 30 minutos
        programarAutoCancelacion(reserva.getReservaVueloId());
        
        // Guardar reserva
        Reserva savedReserva = reservaRepository.save(reserva);
        
        return reservaMapper.toDTO(savedReserva);
    }

    /**
     * Confirmar reserva después del procesamiento bancario
     * PUT /v1/vuelos/reservas/{reservaId}/confirmar
     */
    public ReservaDTO confirmarReserva(String reservaVueloId, String transaccionBancariaId, String metodoPago) {
        Reserva reserva = reservaRepository.findById(reservaVueloId)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
            
        // Validar que la reserva esté en estado PENDIENTE
        if (!"PENDIENTE".equals(reserva.getEstado())) {
            throw new IllegalStateException("La reserva no está en estado pendiente");
        }
        
        // Validar que no haya expirado
        if (LocalDateTime.now().isAfter(reserva.getFechaExpiracion())) {
            // Liberar disponibilidad antes de marcar como expirada
            liberarDisponibilidadVuelo(reserva);
            reserva.setEstado("EXPIRADA");
            reservaRepository.save(reserva);
            throw new IllegalStateException("La reserva ha expirado");
        }
        
        // Confirmar la reserva
        reserva.setReservaConfirmadaId(generarPNR());
        reserva.setEstado("CONFIRMADA");
        reserva.setFechaConfirmacion(LocalDateTime.now());
        reserva.setTransaccionId(transaccionBancariaId);
        reserva.setObservaciones("Reserva confirmada - Método: " + metodoPago);
        reserva.setUrlComprobante("https://aerolinea.com/comprobantes/" + reserva.getReservaConfirmadaId() + ".pdf");
        
        // Cancelar timer de expiración
        cancelarTimerExpiracion(reserva.getReservaVueloId());
        
        Reserva savedReserva = reservaRepository.save(reserva);
        return reservaMapper.toDTO(savedReserva);
    }

    /**
     * Confirmar o denegar reserva según especificaciones del ecosistema
     * POST /v1/vuelos/reservas/confirmar (método legacy - usar confirmarReserva)
     */
    public ReservaDTO confirmarODenegarReserva(String reservaVueloId, String transaccionId, Double precioTotalConfirmado, String estado) {
        Reserva reserva = reservaRepository.findById(reservaVueloId)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
            
        // Validar que la reserva esté en estado PENDIENTE
        if (!"PENDIENTE".equals(reserva.getEstado())) {
            throw new IllegalStateException("La reserva no está en estado pendiente");
        }
        
        // Validar que no haya expirado
        if (LocalDateTime.now().isAfter(reserva.getFechaExpiracion())) {
            liberarDisponibilidadVuelo(reserva);
            reserva.setEstado("EXPIRADA");
            reservaRepository.save(reserva);
            throw new IllegalStateException("La reserva ha expirado");
        }
        
        // Validar que el precio coincida
        if (!precioTotalConfirmado.equals(reserva.getPrecioTotal())) {
            throw new IllegalArgumentException("El precio confirmado no coincide con el precio original");
        }
        
        if ("CONFIRMADO".equals(estado)) {
            // Confirmar la reserva
            reserva.setReservaConfirmadaId(generarPNR());
            reserva.setEstado("CONFIRMADA");
            reserva.setFechaConfirmacion(LocalDateTime.now());
            reserva.setTransaccionId(transaccionId);
            reserva.setObservaciones("Reserva confirmada exitosamente");
            reserva.setUrlComprobante("https://aerolinea.com/comprobantes/" + reserva.getReservaConfirmadaId() + ".pdf");
            
            // Cancelar timer de expiración
            cancelarTimerExpiracion(reserva.getReservaVueloId());
            
        } else if ("DENEGADO".equals(estado)) {
            // Denegar la reserva - liberar disponibilidad
            liberarDisponibilidadVuelo(reserva);
            
            reserva.setEstado("CANCELADA");
            reserva.setFechaCancelacion(LocalDateTime.now());
            reserva.setTransaccionId(transaccionId);
            reserva.setObservaciones("Reserva denegada por el sistema bancario");
            
            // Cancelar timer de expiración
            cancelarTimerExpiracion(reserva.getReservaVueloId());
        }
        
        Reserva savedReserva = reservaRepository.save(reserva);
        return reservaMapper.toDTO(savedReserva);
    }
    
    /**
     * Cancelar pre-reserva
     * POST /v1/vuelos/reservas/cancelar
     */
    public ReservaDTO cancelarPreReserva(String reservaVueloId, String observaciones) {
        Reserva reserva = reservaRepository.findById(reservaVueloId)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
            
        // Validar estado (puede cancelarse si está PENDIENTE o CONFIRMADA)
        if ("CANCELADA".equals(reserva.getEstado()) || "EXPIRADA".equals(reserva.getEstado())) {
            throw new IllegalStateException("La reserva ya está cancelada o expirada");
        }
        
        // Liberar disponibilidad del vuelo
        liberarDisponibilidadVuelo(reserva);
        
        // Procesar cancelación
        reserva.setEstado("CANCELADA");
        reserva.setFechaCancelacion(LocalDateTime.now());
        reserva.setObservaciones(observaciones != null ? observaciones : "Reserva cancelada");
        
        // Cancelar timer si existe
        cancelarTimerExpiracion(reserva.getReservaVueloId());
        
        Reserva savedReserva = reservaRepository.save(reserva);
        return reservaMapper.toDTO(savedReserva);
    }
    
    public ReservaDTO consultarReserva(String reservaVueloId) {
        Reserva reserva = reservaRepository.findById(reservaVueloId)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        return reservaMapper.toDTO(reserva);
    }

    public List<ReservaDTO> listarReservas() {
        return reservaRepository.findAll().stream()
                .map(reservaMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private String generarReservaVueloId() {
        return "RSV" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
    
    private String generarPNR() {
        return "PNR" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
    
    /**
     * Liberar disponibilidad del vuelo cuando se cancela una reserva
     */
    private void liberarDisponibilidadVuelo(Reserva reserva) {
        Vuelo vuelo = reserva.getVuelo();
        if (vuelo != null) {
            // Restablecer la disponibilidad del vuelo
            vuelo.setDisponibilidad(vuelo.getDisponibilidad() + reserva.getNumPasajeros());
            vueloRepository.save(vuelo);
        }
    }
    
    private void programarAutoCancelacion(String reservaVueloId) {
        ScheduledFuture<?> task = scheduler.schedule(() -> {
            try {
                Reserva reserva = reservaRepository.findById(reservaVueloId).orElse(null);
                if (reserva != null && "PENDIENTE".equals(reserva.getEstado())) {
                    // Liberar disponibilidad del vuelo
                    liberarDisponibilidadVuelo(reserva);
                    
                    // Marcar como expirada
                    reserva.setEstado("EXPIRADA");
                    reserva.setObservaciones("Reserva expirada automáticamente - tiempo límite agotado");
                    reservaRepository.save(reserva);
                }
            } catch (Exception e) {
                // Log error but don't propagate
                System.err.println("Error en auto-cancelación: " + e.getMessage());
            } finally {
                expirationTasks.remove(reservaVueloId);
            }
        }, 30, TimeUnit.MINUTES);
        
        expirationTasks.put(reservaVueloId, task);
    }
    
    private void cancelarTimerExpiracion(String reservaVueloId) {
        ScheduledFuture<?> task = expirationTasks.remove(reservaVueloId);
        if (task != null && !task.isDone()) {
            task.cancel(false);
        }
    }

}