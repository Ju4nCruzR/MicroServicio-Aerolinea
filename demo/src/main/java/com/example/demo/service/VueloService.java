package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.VueloDTO;
import com.example.demo.entity.Vuelo;
import com.example.demo.mapper.VueloMapper;
import com.example.demo.repository.AeropuertoRepository;
import com.example.demo.repository.ReservaRepository;
import com.example.demo.repository.VueloRepository;

@Service
public class VueloService {

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private AeropuertoRepository aeropuertoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VueloMapper vueloMapper;

    public VueloDTO crearVuelo(Vuelo vuelo) {
        // VALIDACIONES OBLIGATORIAS: ORIGEN Y DESTINO
        if (vuelo.getOrigen() == null || vuelo.getOrigen().getCodigoIATA() == null || vuelo.getOrigen().getCodigoIATA().trim().isEmpty()) {
            throw new IllegalArgumentException("El aeropuerto de origen es obligatorio");
        }
        
        if (vuelo.getDestino() == null || vuelo.getDestino().getCodigoIATA() == null || vuelo.getDestino().getCodigoIATA().trim().isEmpty()) {
            throw new IllegalArgumentException("El aeropuerto de destino es obligatorio");
        }
        
        // Validar que origen y destino sean diferentes
        if (vuelo.getOrigen().getCodigoIATA().equals(vuelo.getDestino().getCodigoIATA())) {
            throw new IllegalArgumentException("El aeropuerto de origen y destino no pueden ser el mismo");
        }

        // Generar UUID v4 si no se proporciona
        if (vuelo.getVueloId() == null) {
            vuelo.setVueloId(UUID.randomUUID());
        }

        // Validar fechas coherentes
        if (vuelo.getFechaSalida() != null && vuelo.getFechaLlegada() != null &&
                vuelo.getFechaSalida().isAfter(vuelo.getFechaLlegada())) {
            throw new IllegalArgumentException("La fecha de salida no puede ser posterior a la fecha de llegada");
        }

        // Validar que la fecha de salida sea futura
        if (vuelo.getFechaSalida() != null && vuelo.getFechaSalida().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de salida debe ser futura");
        }

        // Validar que el UUID del vuelo sea único
        if (vueloRepository.existsById(vuelo.getVueloId())) {
            throw new IllegalArgumentException("Ya existe un vuelo con este ID");
        }

        // Validar que los aeropuertos origen y destino existan
        if (vuelo.getOrigen() != null && !aeropuertoRepository.existsById(vuelo.getOrigen().getCodigoIATA())) {
            throw new IllegalArgumentException("El aeropuerto de origen no existe");
        }
        if (vuelo.getDestino() != null && !aeropuertoRepository.existsById(vuelo.getDestino().getCodigoIATA())) {
            throw new IllegalArgumentException("El aeropuerto de destino no existe");
        }

        // Establecer valores por defecto
        vuelo.setEstado("PROGRAMADO");
        if (vuelo.getDisponibilidad() == null && vuelo.getCapacidadTotal() != null) {
            vuelo.setDisponibilidad(vuelo.getCapacidadTotal());
        }
        if (vuelo.getMoneda() == null) {
            vuelo.setMoneda("COP");
        }

        Vuelo savedVuelo = vueloRepository.save(vuelo);
        return vueloMapper.toDTO(savedVuelo);
    }

    public VueloDTO consultarVuelo(UUID idVuelo) {
        Vuelo vuelo = vueloRepository.findById(idVuelo).orElse(null);
        return vuelo != null ? vueloMapper.toDTO(vuelo) : null;
    }

    public VueloDTO actualizarVuelo(Vuelo vuelo) {
        // VALIDACIONES OBLIGATORIAS: ORIGEN Y DESTINO
        if (vuelo.getOrigen() == null || vuelo.getOrigen().getCodigoIATA() == null || vuelo.getOrigen().getCodigoIATA().trim().isEmpty()) {
            throw new IllegalArgumentException("El aeropuerto de origen es obligatorio");
        }
        
        if (vuelo.getDestino() == null || vuelo.getDestino().getCodigoIATA() == null || vuelo.getDestino().getCodigoIATA().trim().isEmpty()) {
            throw new IllegalArgumentException("El aeropuerto de destino es obligatorio");
        }
        
        // Validar que origen y destino sean diferentes
        if (vuelo.getOrigen().getCodigoIATA().equals(vuelo.getDestino().getCodigoIATA())) {
            throw new IllegalArgumentException("El aeropuerto de origen y destino no pueden ser el mismo");
        }
        
        // Validar que los aeropuertos existen
        if (!aeropuertoRepository.existsById(vuelo.getOrigen().getCodigoIATA())) {
            throw new IllegalArgumentException("El aeropuerto de origen no existe: " + vuelo.getOrigen().getCodigoIATA());
        }
        if (!aeropuertoRepository.existsById(vuelo.getDestino().getCodigoIATA())) {
            throw new IllegalArgumentException("El aeropuerto de destino no existe: " + vuelo.getDestino().getCodigoIATA());
        }

        // Validar fechas coherentes
        if (vuelo.getFechaSalida() != null && vuelo.getFechaLlegada() != null &&
                vuelo.getFechaSalida().isAfter(vuelo.getFechaLlegada())) {
            throw new IllegalArgumentException("La fecha de salida no puede ser posterior a la fecha de llegada");
        }

        if (vuelo.getDisponibilidad() == null && vuelo.getCapacidadTotal() != null) {
            vuelo.setDisponibilidad(vuelo.getCapacidadTotal());
        }

        // Lógica de actualización si es necesaria
        Vuelo savedVuelo = vueloRepository.save(vuelo);
        return vueloMapper.toDTO(savedVuelo);
    }



    public List<VueloDTO> listarVuelos() {
        return vueloRepository.findAll().stream()
                .map(vueloMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca vuelos según criterios del ecosistema turístico
     * Usa disponibilidad directa del vuelo (sin gestión de asientos específicos)
     */
    public List<VueloDTO> buscarVuelos(String origin, String destination, Integer numPasajeros, 
                                       LocalDate departureDate, LocalDate returnDate, String clase) {
        
        // Validar parámetros obligatorios
        if (origin == null || destination == null || numPasajeros == null || numPasajeros <= 0) {
            throw new IllegalArgumentException("Los parámetros origin, destination y numPasajeros son obligatorios");
        }

        // Validar que los aeropuertos existan
        if (!aeropuertoRepository.existsById(origin) || !aeropuertoRepository.existsById(destination)) {
            throw new IllegalArgumentException("Código de aeropuerto no válido");
        }
        
        // Validar que origen y destino sean diferentes
        if (origin.equals(destination)) {
            throw new IllegalArgumentException("El origen y destino no pueden ser iguales");
        }
        
        // Validar fecha de salida futura
        if (departureDate != null && departureDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de salida debe ser futura");
        }

        return vueloRepository.findAll().stream()
                .filter(vuelo -> {
                    // Filtrar por origen y destino
                    boolean coincideRuta = vuelo.getOrigen() != null && vuelo.getDestino() != null &&
                                          vuelo.getOrigen().getCodigoIATA().equals(origin) && 
                                          vuelo.getDestino().getCodigoIATA().equals(destination);
                    
                    // Filtrar por clase si se especifica
                    boolean coincideClase = clase == null || clase.equals(vuelo.getClase());
                    
                    // Verificar disponibilidad suficiente (simplificado)
                    boolean hayDisponibilidad = vuelo.getDisponibilidad() >= numPasajeros;
                    
                    // Filtrar por fecha de salida si se proporciona
                    boolean coincideFechaSalida = departureDate == null || 
                                                (vuelo.getFechaSalida() != null && 
                                                 vuelo.getFechaSalida().toLocalDate().equals(departureDate));
                    
                    // Solo vuelos activos
                    boolean estaActivo = "PROGRAMADO".equals(vuelo.getEstado());
                    
                    return coincideRuta && coincideClase && hayDisponibilidad && coincideFechaSalida && estaActivo;
                })
                .map(vuelo -> {
                    VueloDTO dto = vueloMapper.toDTO(vuelo);
                    // Usar disponibilidad directa del vuelo
                    dto.setAsientosDisponibles(vuelo.getDisponibilidad());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Método de compatibilidad para búsquedas sin clase específica
     */
    public List<VueloDTO> buscarVuelos(String origin, String destination, Integer numPasajeros, 
                                       LocalDate departureDate, LocalDate returnDate) {
        return buscarVuelos(origin, destination, numPasajeros, departureDate, returnDate, null);
    }

    // ========== MÉTODOS ADMINISTRATIVOS ==========
    
    public List<VueloDTO> listarVuelosAdmin(String origen, String destino, String estado, String aerolinea) {
        return vueloRepository.findAll().stream()
                .filter(vuelo -> {
                    boolean coincideOrigen = origen == null || (vuelo.getOrigen() != null && vuelo.getOrigen().getCodigoIATA().equals(origen));
                    boolean coincideDestino = destino == null || (vuelo.getDestino() != null && vuelo.getDestino().getCodigoIATA().equals(destino));
                    boolean coincideEstado = estado == null || estado.equals(vuelo.getEstado());
                    boolean coincideAerolinea = aerolinea == null || aerolinea.equals(vuelo.getAerolinea());
                    
                    return coincideOrigen && coincideDestino && coincideEstado && coincideAerolinea;
                })
                .map(vueloMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public boolean eliminarVuelo(UUID vueloId) {
        Vuelo vuelo = vueloRepository.findById(vueloId).orElse(null);
        if (vuelo == null) {
            return false;
        }
        
        // Verificar si tiene reservas confirmadas
        boolean tieneReservasConfirmadas = vuelo.getReservas() != null && 
            vuelo.getReservas().stream().anyMatch(reserva -> "CONFIRMADA".equals(reserva.getEstado()));
        
        if (tieneReservasConfirmadas) {
            return false; // No se puede eliminar
        }
        
        // Cambiar estado a CANCELADO en lugar de eliminar
        vuelo.setEstado("CANCELADO");
        vueloRepository.save(vuelo);
        return true;
    }
    
    public VueloDTO ajustarDisponibilidad(UUID vueloId, Integer nuevaDisponibilidad, String motivo) {
        Vuelo vuelo = vueloRepository.findById(vueloId)
            .orElseThrow(() -> new IllegalArgumentException("Vuelo no encontrado"));
            
        if (nuevaDisponibilidad < 0) {
            throw new IllegalArgumentException("La disponibilidad no puede ser negativa");
        }
        
        if (nuevaDisponibilidad > vuelo.getCapacidadTotal()) {
            throw new IllegalArgumentException("La disponibilidad no puede superar la capacidad total");
        }
        
        vuelo.setDisponibilidad(nuevaDisponibilidad);
        Vuelo savedVuelo = vueloRepository.save(vuelo);
        return vueloMapper.toDTO(savedVuelo);
    }
    
    public com.example.demo.dto.ResumenReservasVueloDTO obtenerResumenReservas(UUID vueloId) {
        Vuelo vuelo = vueloRepository.findById(vueloId).orElse(null);
        if (vuelo == null) {
            return null;
        }
        
        com.example.demo.dto.ResumenReservasVueloDTO resumen = new com.example.demo.dto.ResumenReservasVueloDTO();
        resumen.setVueloId(vueloId);
        
        // Obtener reservas usando repository para evitar lazy loading
        List<com.example.demo.entity.Reserva> reservasVuelo = reservaRepository.findAll().stream()
            .filter(r -> r.getVuelo() != null && vueloId.equals(r.getVuelo().getVueloId()))
            .collect(java.util.stream.Collectors.toList());
            
        resumen.setTotalReservas(reservasVuelo.size());
        resumen.setReservasConfirmadas((int) reservasVuelo.stream().filter(r -> "CONFIRMADA".equals(r.getEstado())).count());
        resumen.setReservasPendientes((int) reservasVuelo.stream().filter(r -> "PENDIENTE".equals(r.getEstado())).count());
        resumen.setReservasCanceladas((int) reservasVuelo.stream().filter(r -> "CANCELADA".equals(r.getEstado())).count());
            
        resumen.setPasajerosConfirmados(reservasVuelo.stream()
            .filter(r -> "CONFIRMADA".equals(r.getEstado()))
            .mapToInt(r -> r.getNumPasajeros())
            .sum());
                
        resumen.setIngresosTotales(reservasVuelo.stream()
            .filter(r -> "CONFIRMADA".equals(r.getEstado()))
            .mapToDouble(r -> r.getPrecioTotal())
            .sum());
        
        return resumen;
    }
    
    public VueloDTO cambiarEstadoVuelo(UUID vueloId, String nuevoEstado, String observaciones) {
        Vuelo vuelo = vueloRepository.findById(vueloId)
            .orElseThrow(() -> new IllegalArgumentException("Vuelo no encontrado"));
            
        // Validar transición de estado
        if (!esTransicionValidaVuelo(vuelo.getEstado(), nuevoEstado)) {
            throw new IllegalStateException("Transición de estado no válida: " + vuelo.getEstado() + " -> " + nuevoEstado);
        }
        
        vuelo.setEstado(nuevoEstado);
        Vuelo savedVuelo = vueloRepository.save(vuelo);
        return vueloMapper.toDTO(savedVuelo);
    }
    
    private boolean esTransicionValidaVuelo(String estadoActual, String nuevoEstado) {
        switch (estadoActual) {
            case "PROGRAMADO":
                return "EN_VUELO".equals(nuevoEstado) || "CANCELADO".equals(nuevoEstado);
            case "EN_VUELO":
                return "COMPLETADO".equals(nuevoEstado) || "CANCELADO".equals(nuevoEstado);
            case "COMPLETADO":
            case "CANCELADO":
                return false; // Estados finales
            default:
                return false;
        }
    }

}