package com.example.demo.service;

import com.example.demo.entity.Aeropuerto;
import com.example.demo.dto.AeropuertoDTO;
import com.example.demo.mapper.AeropuertoMapper;
import com.example.demo.repository.AeropuertoRepository;
import com.example.demo.repository.VueloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AeropuertoService {

    @Autowired
    private AeropuertoRepository aeropuertoRepository;
    
    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private AeropuertoMapper aeropuertoMapper;
    
    // Pattern para validar códigos IATA (3 letras mayúsculas)
    private static final Pattern IATA_PATTERN = Pattern.compile("^[A-Z]{3}$");
    
    // Pattern para validar códigos ICAO (4 letras mayúsculas)
    private static final Pattern ICAO_PATTERN = Pattern.compile("^[A-Z]{4}$");

    /**
     * Crear aeropuerto con validaciones de códigos IATA
     */
    public AeropuertoDTO crearAeropuerto(Aeropuerto aeropuerto) {
        // Validar código IATA
        if (aeropuerto.getCodigoIATA() == null || !IATA_PATTERN.matcher(aeropuerto.getCodigoIATA()).matches()) {
            throw new IllegalArgumentException("El código IATA debe tener exactamente 3 letras mayúsculas");
        }
        
        // Validar que el código IATA sea único
        if (aeropuertoRepository.existsById(aeropuerto.getCodigoIATA())) {
            throw new IllegalArgumentException("Ya existe un aeropuerto con el código IATA: " + aeropuerto.getCodigoIATA());
        }
        
        // Validar código ICAO si se proporciona
        if (aeropuerto.getCodigoICAO() != null && !aeropuerto.getCodigoICAO().trim().isEmpty()) {
            if (!ICAO_PATTERN.matcher(aeropuerto.getCodigoICAO()).matches()) {
                throw new IllegalArgumentException("El código ICAO debe tener exactamente 4 letras mayúsculas");
            }
            
            // Validar que el código ICAO sea único
            boolean existeICAO = aeropuertoRepository.findAll().stream()
                .anyMatch(a -> aeropuerto.getCodigoICAO().equals(a.getCodigoICAO()));
            if (existeICAO) {
                throw new IllegalArgumentException("Ya existe un aeropuerto con el código ICAO: " + aeropuerto.getCodigoICAO());
            }
        }
        
        // Validar campos obligatorios
        if (aeropuerto.getNombre() == null || aeropuerto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del aeropuerto es obligatorio");
        }
        
        if (aeropuerto.getCiudad() == null || aeropuerto.getCiudad().trim().isEmpty()) {
            throw new IllegalArgumentException("La ciudad del aeropuerto es obligatoria");
        }
        
        if (aeropuerto.getPais() == null || aeropuerto.getPais().trim().isEmpty()) {
            throw new IllegalArgumentException("El país del aeropuerto es obligatorio");
        }
        
        Aeropuerto savedAeropuerto = aeropuertoRepository.save(aeropuerto);
        return aeropuertoMapper.toDTO(savedAeropuerto);
    }

    /**
     * Consultar aeropuerto por código IATA
     */
    public AeropuertoDTO consultarAeropuerto(String codigoIATA) {
        // Validar formato del código IATA
        if (codigoIATA == null || !IATA_PATTERN.matcher(codigoIATA).matches()) {
            throw new IllegalArgumentException("El código IATA debe tener exactamente 3 letras mayúsculas");
        }
        
        Aeropuerto aeropuerto = aeropuertoRepository.findById(codigoIATA)
            .orElseThrow(() -> new IllegalArgumentException("Aeropuerto no encontrado con código IATA: " + codigoIATA));
        return aeropuertoMapper.toDTO(aeropuerto);
    }
    
    /**
     * Buscar aeropuerto por código ICAO
     */
    public Optional<AeropuertoDTO> buscarPorCodigoICAO(String codigoICAO) {
        if (codigoICAO == null || !ICAO_PATTERN.matcher(codigoICAO).matches()) {
            throw new IllegalArgumentException("El código ICAO debe tener exactamente 4 letras mayúsculas");
        }
        
        return aeropuertoRepository.findAll().stream()
            .filter(a -> codigoICAO.equals(a.getCodigoICAO()))
            .findFirst()
            .map(aeropuertoMapper::toDTO);
    }
    
    /**
     * Buscar aeropuertos por ciudad
     */
    public List<AeropuertoDTO> buscarPorCiudad(String ciudad) {
        return aeropuertoRepository.findAll().stream()
            .filter(a -> a.getCiudad().toLowerCase().contains(ciudad.toLowerCase()))
            .map(aeropuertoMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Buscar aeropuertos por país
     */
    public List<AeropuertoDTO> buscarPorPais(String pais) {
        return aeropuertoRepository.findAll().stream()
            .filter(a -> a.getPais().toLowerCase().contains(pais.toLowerCase()))
            .map(aeropuertoMapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Actualizar aeropuerto (solo permite actualizar nombre, ciudad, país, codigoICAO)
     * El código IATA no se puede cambiar porque es la clave primaria
     */
    public AeropuertoDTO actualizarAeropuerto(Aeropuerto aeropuerto) {
        // Verificar que el aeropuerto existe
        Aeropuerto existente = aeropuertoRepository.findById(aeropuerto.getCodigoIATA())
            .orElseThrow(() -> new IllegalArgumentException("Aeropuerto no encontrado"));
            
        // Validar campos si se proporcionan
        if (aeropuerto.getNombre() != null && !aeropuerto.getNombre().trim().isEmpty()) {
            existente.setNombre(aeropuerto.getNombre());
        }
        
        if (aeropuerto.getCiudad() != null && !aeropuerto.getCiudad().trim().isEmpty()) {
            existente.setCiudad(aeropuerto.getCiudad());
        }
        
        if (aeropuerto.getPais() != null && !aeropuerto.getPais().trim().isEmpty()) {
            existente.setPais(aeropuerto.getPais());
        }
        
        // Validar y actualizar código ICAO si se proporciona
        if (aeropuerto.getCodigoICAO() != null && !aeropuerto.getCodigoICAO().trim().isEmpty()) {
            if (!ICAO_PATTERN.matcher(aeropuerto.getCodigoICAO()).matches()) {
                throw new IllegalArgumentException("El código ICAO debe tener exactamente 4 letras mayúsculas");
            }
            
            // Validar que el nuevo código ICAO sea único (excluyendo el actual)
            boolean existeICAO = aeropuertoRepository.findAll().stream()
                .anyMatch(a -> !a.getCodigoIATA().equals(aeropuerto.getCodigoIATA()) && 
                              aeropuerto.getCodigoICAO().equals(a.getCodigoICAO()));
            if (existeICAO) {
                throw new IllegalArgumentException("Ya existe otro aeropuerto con el código ICAO: " + aeropuerto.getCodigoICAO());
            }
            
            existente.setCodigoICAO(aeropuerto.getCodigoICAO());
        }
        
        Aeropuerto savedAeropuerto = aeropuertoRepository.save(existente);
        return aeropuertoMapper.toDTO(savedAeropuerto);
    }

    /**
     * Eliminar aeropuerto (solo si no tiene vuelos asociados)
     */
    public void eliminarAeropuerto(String codigoIATA) {
        // Validar que el aeropuerto existe
        if (!aeropuertoRepository.existsById(codigoIATA)) {
            throw new IllegalArgumentException("Aeropuerto no encontrado");
        }
        
        // REGLA DE NEGOCIO: No eliminar aeropuertos con vuelos asociados
        long vuelosOrigen = vueloRepository.findAll().stream()
            .filter(v -> v.getOrigen() != null && codigoIATA.equals(v.getOrigen().getCodigoIATA()))
            .count();
            
        long vuelosDestino = vueloRepository.findAll().stream()
            .filter(v -> v.getDestino() != null && codigoIATA.equals(v.getDestino().getCodigoIATA()))
            .count();
            
        if (vuelosOrigen > 0 || vuelosDestino > 0) {
            throw new IllegalStateException("No se puede eliminar un aeropuerto que tiene vuelos asociados");
        }
        
        aeropuertoRepository.deleteById(codigoIATA);
    }
    
    /**
     * Validar si un código IATA es válido y existe
     */
    public boolean validarCodigoIATA(String codigoIATA) {
        if (codigoIATA == null || !IATA_PATTERN.matcher(codigoIATA).matches()) {
            return false;
        }
        return aeropuertoRepository.existsById(codigoIATA);
    }

    public List<AeropuertoDTO> listarAeropuertos() {
        return aeropuertoRepository.findAll().stream()
                .map(aeropuertoMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtener aeropuertos populares (con más vuelos)
     */
    public List<AeropuertoDTO> obtenerAeropuertosPopulares(int limite) {
        return aeropuertoRepository.findAll().stream()
            .sorted((a1, a2) -> {
                long vuelos1 = vueloRepository.findAll().stream()
                    .filter(v -> (v.getOrigen() != null && a1.getCodigoIATA().equals(v.getOrigen().getCodigoIATA())) ||
                                (v.getDestino() != null && a1.getCodigoIATA().equals(v.getDestino().getCodigoIATA())))
                    .count();
                long vuelos2 = vueloRepository.findAll().stream()
                    .filter(v -> (v.getOrigen() != null && a2.getCodigoIATA().equals(v.getOrigen().getCodigoIATA())) ||
                                (v.getDestino() != null && a2.getCodigoIATA().equals(v.getDestino().getCodigoIATA())))
                    .count();
                return Long.compare(vuelos2, vuelos1); // Orden descendente
            })
            .limit(limite)
            .map(aeropuertoMapper::toDTO)
            .collect(Collectors.toList());
    }

}