package com.example.demo.service;

import com.example.demo.entity.Reserva;
import com.example.demo.entity.Vuelo;
import com.example.demo.entity.Asiento;
import com.example.demo.dto.ReservaDTO;
import com.example.demo.mapper.ReservaMapper;
import com.example.demo.repository.ReservaRepository;
import com.example.demo.repository.VueloRepository;
import com.example.demo.repository.PasajeroRepository;
import com.example.demo.repository.AsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private AsientoRepository asientoRepository;

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
        // Validar que el vuelo exista y esté en estado PROGRAMADO
        Vuelo vuelo = vueloRepository.findById(vueloId)
            .orElseThrow(() -> new IllegalArgumentException("Vuelo no encontrado"));
            
        if (!"PROGRAMADO".equals(vuelo.getEstado())) {
            throw new IllegalArgumentException("El vuelo no está disponible para reservas");
        }
        
        // Validar que la fecha del vuelo sea futura
        if (vuelo.getFechaSalida().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se pueden hacer reservas para vuelos pasados");
        }
        
        // Verificar disponibilidad suficiente
        long asientosOcupados = asientoRepository.findAll().stream()
            .filter(a -> a.getVuelo() != null && a.getVuelo().getVueloId().equals(vueloId))
            .filter(a -> !"DISPONIBLE".equals(a.getEstado()))
            .count();
            
        int disponibilidadReal = vuelo.getDisponibilidad() - (int) asientosOcupados;
        if (disponibilidadReal < numPasajeros) {
            throw new IllegalStateException("No hay suficientes asientos disponibles");
        }
        
        // Crear la pre-reserva
        Reserva reserva = new Reserva();
        reserva.setReservaVueloId(generarReservaVueloId());
        reserva.setVuelo(vuelo);
        reserva.setNumPasajeros(numPasajeros);
        reserva.setContactoReserva(contactoReserva);
        reserva.setDocumentoContacto(documentoContacto);
        reserva.setPrecioTotal(vuelo.getPrecio() * numPasajeros);
        reserva.setEstado("PENDIENTE");
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setFechaExpiracion(reserva.getFechaCreacion().plusMinutes(30));
        reserva.setObservaciones("Reserva válida por 30 minutos. Confirme antes de la expiración.");
        
        // Bloquear asientos temporalmente
        bloquearAsientosTemporalmente(vuelo, numPasajeros, reserva.getReservaVueloId());
        
        // Guardar reserva
        Reserva savedReserva = reservaRepository.save(reserva);
        
        // Programar auto-cancelación
        programarAutoCancelacion(savedReserva.getReservaVueloId());
        
        return reservaMapper.toDTO(savedReserva);
    }

    /**
     * Confirmar o denegar reserva según especificaciones del ecosistema
     * POST /v1/vuelos/reservas/confirmar
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
            
            // Confirmar asientos bloqueados → OCUPADO
            confirmarAsientosBloqueados(reserva.getReservaVueloId());
            
            // Cancelar timer de expiración
            cancelarTimerExpiracion(reserva.getReservaVueloId());
            
        } else if ("DENEGADO".equals(estado)) {
            // Denegar la reserva
            reserva.setEstado("CANCELADA");
            reserva.setFechaCancelacion(LocalDateTime.now());
            reserva.setTransaccionId(transaccionId);
            
            // Liberar asientos bloqueados
            liberarAsientosBloqueados(reserva.getReservaVueloId());
            
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
    public ReservaDTO cancelarPreReserva(String reservaVueloId, String transaccionId) {
        Reserva reserva = reservaRepository.findById(reservaVueloId)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
            
        // Validar estado (puede cancelarse si está PENDIENTE o CONFIRMADA)
        if ("CANCELADA".equals(reserva.getEstado()) || "EXPIRADA".equals(reserva.getEstado())) {
            throw new IllegalStateException("La reserva ya está cancelada o expirada");
        }
        
        // Procesar cancelación
        reserva.setEstado("CANCELADA");
        reserva.setFechaCancelacion(LocalDateTime.now());
        if (transaccionId != null) {
            reserva.setTransaccionId(transaccionId);
        }
        
        // Liberar asientos
        liberarAsientosBloqueados(reserva.getReservaVueloId());
        
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
    
    private void bloquearAsientosTemporalmente(Vuelo vuelo, int numPasajeros, String reservaVueloId) {
        List<Asiento> asientosDisponibles = asientoRepository.findAll().stream()
            .filter(a -> a.getVuelo() != null && a.getVuelo().getVueloId().equals(vuelo.getVueloId()))
            .filter(a -> "DISPONIBLE".equals(a.getEstado()))
            .filter(a -> vuelo.getClase().equals(a.getClase())) // Misma clase
            .limit(numPasajeros)
            .collect(Collectors.toList());
            
        if (asientosDisponibles.size() < numPasajeros) {
            throw new IllegalStateException("No hay suficientes asientos disponibles en la clase solicitada");
        }
        
        for (Asiento asiento : asientosDisponibles) {
            asiento.setEstado("BLOQUEADO");
            asiento.getReserva().setReservaVueloId(reservaVueloId); // Asociar con reserva
            asientoRepository.save(asiento);
        }
    }
    
    private void confirmarAsientosBloqueados(String reservaVueloId) {
        List<Asiento> asientosBloqueados = asientoRepository.findAll().stream()
            .filter(a -> a.getReserva() != null && reservaVueloId.equals(a.getReserva().getReservaVueloId()))
            .filter(a -> "BLOQUEADO".equals(a.getEstado()))
            .collect(Collectors.toList());
            
        for (Asiento asiento : asientosBloqueados) {
            asiento.setEstado("OCUPADO");
            asientoRepository.save(asiento);
        }
    }
    
    private void liberarAsientosBloqueados(String reservaVueloId) {
        List<Asiento> asientosReserva = asientoRepository.findAll().stream()
            .filter(a -> a.getReserva() != null && reservaVueloId.equals(a.getReserva().getReservaVueloId()))
            .filter(a -> "BLOQUEADO".equals(a.getEstado()) || "OCUPADO".equals(a.getEstado()))
            .collect(Collectors.toList());
            
        for (Asiento asiento : asientosReserva) {
            asiento.setEstado("DISPONIBLE");
            asiento.setReserva(null); // Desasociar de la reserva
            asientoRepository.save(asiento);
        }
    }
    
    private void programarAutoCancelacion(String reservaVueloId) {
        ScheduledFuture<?> task = scheduler.schedule(() -> {
            try {
                Reserva reserva = reservaRepository.findById(reservaVueloId).orElse(null);
                if (reserva != null && "PENDIENTE".equals(reserva.getEstado())) {
                    reserva.setEstado("EXPIRADA");
                    liberarAsientosBloqueados(reservaVueloId);
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