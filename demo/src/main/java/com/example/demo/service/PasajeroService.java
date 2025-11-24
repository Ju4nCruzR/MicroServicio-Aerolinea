package com.example.demo.service;

import com.example.demo.entity.Pasajero;
import com.example.demo.entity.Asiento;
import com.example.demo.dto.PasajeroDTO;
import com.example.demo.mapper.PasajeroMapper;
import com.example.demo.repository.PasajeroRepository;
import com.example.demo.repository.AsientoRepository;
import com.example.demo.repository.VueloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class PasajeroService {

    @Autowired
    private PasajeroRepository pasajeroRepository;
    
    @Autowired
    private AsientoRepository asientoRepository;
    
    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private PasajeroMapper pasajeroMapper;

    /**
     * Crear/actualizar pasajero con clientId del ecosistema Turismo
     * Este método maneja pasajeros enviados desde el microservicio Turismo
     */
    public PasajeroDTO crearPasajero(Pasajero pasajero) {
        // Validar clientId (requerido del ecosistema Turismo)
        if (pasajero.getClientId() == null || pasajero.getClientId().trim().isEmpty()) {
            throw new IllegalArgumentException("ClientId es obligatorio (debe venir del microservicio Turismo)");
        }
        
        // Validar que el documento sea único
        boolean existeDocumento = pasajeroRepository.findAll().stream()
                .anyMatch(p -> !p.getClientId().equals(pasajero.getClientId()) && 
                              p.getNumeroDocumento().equals(pasajero.getNumeroDocumento()));
        if (existeDocumento) {
            throw new IllegalArgumentException("Ya existe un pasajero con este número de documento");
        }

        Pasajero savedPasajero = pasajeroRepository.save(pasajero);
        return pasajeroMapper.toDTO(savedPasajero);
    }

    public PasajeroDTO consultarPasajero(String clientId) {
        Pasajero pasajero = pasajeroRepository.findById(clientId)
            .orElseThrow(() -> new IllegalArgumentException("Pasajero no encontrado"));
        return pasajeroMapper.toDTO(pasajero);
    }
    
    /**
     * Buscar pasajero por número de documento
     */
    public Optional<PasajeroDTO> buscarPorDocumento(String numeroDocumento) {
        return pasajeroRepository.findAll().stream()
            .filter(p -> p.getNumeroDocumento().equals(numeroDocumento))
            .findFirst()
            .map(pasajeroMapper::toDTO);
    }

    /**
     * Asignar asiento a pasajero - Endpoint operativo
     * POST /v1/vuelos/asignar-pasajero
     */
    public boolean asignarAsientoAPasajero(String flightId, String clientId, String reservationId, String asiento) {
        // Validar que el vuelo existe
        if (!vueloRepository.existsById(flightId)) {
            throw new IllegalArgumentException("Vuelo no encontrado");
        }
        
        // Validar que el pasajero existe
        if (!pasajeroRepository.existsById(clientId)) {
            throw new IllegalArgumentException("Pasajero no encontrado");
        }
            
        // Buscar el asiento específico
        Optional<Asiento> asientoOpt = asientoRepository.findAll().stream()
            .filter(a -> a.getVuelo() != null && a.getVuelo().getVueloId().equals(flightId))
            .filter(a -> a.getNumero().equals(asiento))
            .findFirst();
            
        if (!asientoOpt.isPresent()) {
            throw new IllegalArgumentException("Asiento no encontrado en el vuelo");
        }
        
        Asiento asientoEntity = asientoOpt.get();
        
        // Validar que el asiento no esté ya asignado
        if ("ASIGNADO".equals(asientoEntity.getEstado())) {
            throw new IllegalStateException("El asiento ya está asignado");
        }
        
        // Validar que el asiento esté ocupado (reserva confirmada)
        if (!"OCUPADO".equals(asientoEntity.getEstado())) {
            throw new IllegalStateException("El asiento debe estar ocupado (reserva confirmada) para poder asignarse");
        }
        
        // Realizar la asignación
        asientoEntity.setEstado("ASIGNADO");
        asientoEntity.setClientIdAsignado(clientId);
        asientoRepository.save(asientoEntity);
        
        return true;
    }

    public void eliminarPasajero(String clientId) {
        // Verificar que no tenga asientos asignados activos
        long asientosAsignados = asientoRepository.findAll().stream()
            .filter(a -> clientId.equals(a.getClientIdAsignado()))
            .filter(a -> "ASIGNADO".equals(a.getEstado()))
            .count();
            
        if (asientosAsignados > 0) {
            throw new IllegalStateException("No se puede eliminar un pasajero con asientos asignados activos");
        }
        
        pasajeroRepository.deleteById(clientId);
    }

    public List<PasajeroDTO> listarPasajeros() {
        return pasajeroRepository.findAll().stream()
                .map(pasajeroMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar pasajeros de una reserva específica
     * Nota: Con la entidad simplificada, esta funcionalidad debe ser manejada
     * por el servicio de reservas que mantenga la relación
     */
    public List<PasajeroDTO> listarPasajerosPorReserva(String reservaVueloId) {
        // Esta funcionalidad ahora debe ser manejada por ReservaService
        // ya que Pasajero no mantiene relación directa con Reserva
        throw new UnsupportedOperationException("Esta funcionalidad debe ser consultada a través de ReservaService");
    }
    
    /**
     * Verificar si un clientId existe en el sistema
     */
    public boolean existeClientId(String clientId) {
        return pasajeroRepository.existsById(clientId);
    }

}