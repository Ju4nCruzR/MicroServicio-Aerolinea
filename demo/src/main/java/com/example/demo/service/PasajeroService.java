package com.example.demo.service;

import com.example.demo.entity.Pasajero;
import com.example.demo.dto.PasajeroDTO;
import com.example.demo.mapper.PasajeroMapper;
import com.example.demo.repository.PasajeroRepository;
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

    public void eliminarPasajero(String clientId) {
        // Validación simplificada - solo verificar que el pasajero existe
        if (!pasajeroRepository.existsById(clientId)) {
            throw new IllegalArgumentException("Pasajero no encontrado");
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

    // ========== MÉTODOS ADMINISTRATIVOS ==========
    
    public List<PasajeroDTO> listarPasajeros(String tipoDocumento, String numeroDocumento, String nombre) {
        return pasajeroRepository.findAll().stream()
                .filter(pasajero -> {
                    // tipoDocumento no está en la entidad, ignorar este filtro
                    boolean coincideNumero = numeroDocumento == null || numeroDocumento.equals(pasajero.getNumeroDocumento());
                    boolean coincideNombre = nombre == null || 
                        (pasajero.getNombre() != null && pasajero.getNombre().toLowerCase().contains(nombre.toLowerCase()));
                    
                    return coincideNumero && coincideNombre;
                })
                .map(pasajeroMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    public PasajeroDTO consultarPasajero(java.util.UUID pasajeroId) {
        // Convertir UUID a String para buscar por clientId
        String clientId = pasajeroId.toString();
        return consultarPasajero(clientId);
    }
    
    public PasajeroDTO actualizarPasajero(Pasajero pasajero) {
        if (!pasajeroRepository.existsById(pasajero.getClientId())) {
            return null; // No existe
        }
        
        Pasajero savedPasajero = pasajeroRepository.save(pasajero);
        return pasajeroMapper.toDTO(savedPasajero);
    }
    
    public boolean eliminarPasajero(java.util.UUID pasajeroId) {
        String clientId = pasajeroId.toString();
        
        if (!pasajeroRepository.existsById(clientId)) {
            return false;
        }
        
        try {
            pasajeroRepository.deleteById(clientId);
            return true;
        } catch (Exception e) {
            // Si hay restricciones de integridad referencial
            return false;
        }
    }
    
    public PasajeroDTO buscarPorDocumento(String tipoDocumento, String numeroDocumento) {
        // tipoDocumento no está en la entidad, solo buscar por número
        return pasajeroRepository.findAll().stream()
                .filter(p -> numeroDocumento.equals(p.getNumeroDocumento()))
                .findFirst()
                .map(pasajeroMapper::toDTO)
                .orElse(null);
    }

}