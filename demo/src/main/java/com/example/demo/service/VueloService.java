package com.example.demo.service;

import com.example.demo.entity.Vuelo;
import com.example.demo.entity.Aeropuerto;
import com.example.demo.dto.VueloDTO;
import com.example.demo.mapper.VueloMapper;
import com.example.demo.repository.VueloRepository;
import com.example.demo.repository.AeropuertoRepository;
import com.example.demo.repository.AsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VueloService {

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private AeropuertoRepository aeropuertoRepository;

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private VueloMapper vueloMapper;

    public VueloDTO crearVuelo(Vuelo vuelo) {
        // Generar UUID v4 si no se proporciona
        if (vuelo.getVueloId() == null || vuelo.getVueloId().isEmpty()) {
            vuelo.setVueloId(UUID.randomUUID().toString());
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

    public VueloDTO consultarVuelo(String idVuelo) {
        Vuelo vuelo = vueloRepository.findById(idVuelo).orElse(null);
        return vuelo != null ? vueloMapper.toDTO(vuelo) : null;
    }

    public VueloDTO actualizarVuelo(Vuelo vuelo) {
        // Validar fechas coherentes
        if (vuelo.getFechaSalida() != null && vuelo.getFechaLlegada() != null &&
                vuelo.getFechaSalida().isAfter(vuelo.getFechaLlegada())) {
            throw new IllegalArgumentException("La fecha de salida no puede ser posterior a la fecha de llegada");
        }

        // Lógica de actualización si es necesaria
        Vuelo savedVuelo = vueloRepository.save(vuelo);
        return vueloMapper.toDTO(savedVuelo);
    }

    public void eliminarVuelo(String idVuelo) {
        Vuelo vuelo = vueloRepository.findById(idVuelo)
            .orElseThrow(() -> new IllegalArgumentException("Vuelo no encontrado"));
        
        // REGLA DE NEGOCIO: No eliminar vuelos con asignaciones operativas activas
        long asientosAsignados = asientoRepository.findAll().stream()
            .filter(a -> a.getVuelo() != null && a.getVuelo().getVueloId().equals(idVuelo))
            .filter(a -> "ASIGNADO".equals(a.getEstado()) || "OCUPADO".equals(a.getEstado()))
            .count();
            
        if (asientosAsignados > 0) {
            throw new IllegalStateException("No se puede eliminar un vuelo con asignaciones operativas activas");
        }
        
        // REGLA DE NEGOCIO: No eliminar vuelos en estado EN_VUELO
        if ("EN_VUELO".equals(vuelo.getEstado())) {
            throw new IllegalStateException("No se puede eliminar un vuelo en estado EN_VUELO");
        }
        
        // En lugar de eliminar físicamente, cambiar estado a CANCELADO
        vuelo.setEstado("CANCELADO");
        vueloRepository.save(vuelo);
    }

    public List<VueloDTO> listarVuelos() {
        return vueloRepository.findAll().stream()
                .map(vueloMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca vuelos según criterios del ecosistema turístico
     * Calcula disponibilidad real restando asientos ocupados/bloqueados
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
                    
                    // Calcular disponibilidad real
                    long asientosOcupados = asientoRepository.findAll().stream()
                        .filter(a -> a.getVuelo() != null && a.getVuelo().getVueloId().equals(vuelo.getVueloId()))
                        .filter(a -> "OCUPADO".equals(a.getEstado()) || "BLOQUEADO".equals(a.getEstado()) || "ASIGNADO".equals(a.getEstado()))
                        .filter(a -> clase == null || clase.equals(a.getClase()))
                        .count();
                    
                    int disponibilidadReal = vuelo.getDisponibilidad() - (int) asientosOcupados;
                    boolean hayDisponibilidad = disponibilidadReal >= numPasajeros;
                    
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
                    // Actualizar disponibilidad real en el DTO
                    long asientosOcupados = asientoRepository.findAll().stream()
                        .filter(a -> a.getVuelo() != null && a.getVuelo().getVueloId().equals(vuelo.getVueloId()))
                        .filter(a -> "OCUPADO".equals(a.getEstado()) || "BLOQUEADO".equals(a.getEstado()) || "ASIGNADO".equals(a.getEstado()))
                        .filter(a -> clase == null || clase.equals(a.getClase()))
                        .count();
                    dto.setAsientosDisponibles(vuelo.getDisponibilidad() - (int) asientosOcupados);
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

}