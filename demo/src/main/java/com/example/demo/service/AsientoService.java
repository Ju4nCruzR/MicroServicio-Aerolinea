package com.example.demo.service;

import com.example.demo.entity.Asiento;
import com.example.demo.entity.Vuelo;
import com.example.demo.entity.Reserva;
import com.example.demo.dto.AsientoDTO;
import com.example.demo.mapper.AsientoMapper;
import com.example.demo.repository.AsientoRepository;
import com.example.demo.repository.VueloRepository;
import com.example.demo.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class AsientoService {

    @Autowired
    private AsientoRepository asientoRepository;
    
    @Autowired
    private VueloRepository vueloRepository;
    
    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private AsientoMapper asientoMapper;

    /**
     * Crear asientos para un vuelo (generalmente se hace al crear el vuelo)
     */
    public AsientoDTO crearAsiento(Asiento asiento) {
        // Validar que el vuelo existe
        if (asiento.getVuelo() == null || !vueloRepository.existsById(asiento.getVuelo().getVueloId())) {
            throw new IllegalArgumentException("El vuelo especificado no existe");
        }
        
        // Validar que no exista ya un asiento con el mismo número en el vuelo
        boolean existeAsiento = asientoRepository.findAll().stream()
            .anyMatch(a -> a.getVuelo() != null && 
                          a.getVuelo().getVueloId().equals(asiento.getVuelo().getVueloId()) &&
                          a.getNumero().equals(asiento.getNumero()));
                          
        if (existeAsiento) {
            throw new IllegalArgumentException("Ya existe un asiento con este número en el vuelo");
        }
        
        // Establecer estado inicial
        if (asiento.getEstado() == null) {
            asiento.setEstado("DISPONIBLE");
        }
        
        Asiento savedAsiento = asientoRepository.save(asiento);
        return asientoMapper.toDTO(savedAsiento);
    }
    
    /**
     * Generar asientos automáticamente para un vuelo
     */
    public void generarAsientosParaVuelo(String vueloId, int capacidadTotal, String claseVuelo) {
        Vuelo vuelo = vueloRepository.findById(vueloId)
            .orElseThrow(() -> new IllegalArgumentException("Vuelo no encontrado"));
            
        // Generar asientos según capacidad
        int filas = (capacidadTotal / 6) + (capacidadTotal % 6 > 0 ? 1 : 0); // 6 asientos por fila (A-F)
        String[] letras = {"A", "B", "C", "D", "E", "F"};
        
        int asientoCount = 0;
        for (int fila = 1; fila <= filas && asientoCount < capacidadTotal; fila++) {
            for (String letra : letras) {
                if (asientoCount >= capacidadTotal) break;
                
                Asiento asiento = new Asiento();
                asiento.setVuelo(vuelo);
                asiento.setNumero(fila + letra);
                asiento.setClase(claseVuelo);
                asiento.setEstado("DISPONIBLE");
                asiento.setPrecio(vuelo.getPrecio()); // Mismo precio que el vuelo
                
                asientoRepository.save(asiento);
                asientoCount++;
            }
        }
    }

    /**
     * Consultar asiento por ID de vuelo y número de asiento
     */
    public Optional<AsientoDTO> consultarAsiento(String vueloId, String numeroAsiento) {
        return asientoRepository.findAll().stream()
            .filter(a -> a.getVuelo() != null && a.getVuelo().getVueloId().equals(vueloId))
            .filter(a -> a.getNumero().equals(numeroAsiento))
            .findFirst()
            .map(asientoMapper::toDTO);
    }
    
    /**
     * Listar asientos disponibles para un vuelo y clase
     */
    public List<AsientoDTO> listarAsientosDisponibles(String vueloId, String clase) {
        return asientoRepository.findAll().stream()
            .filter(a -> a.getVuelo() != null && a.getVuelo().getVueloId().equals(vueloId))
            .filter(a -> clase == null || clase.equals(a.getClase()))
            .filter(a -> "DISPONIBLE".equals(a.getEstado()))
            .map(asientoMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Bloquear asientos temporalmente para una reserva
     */
    public boolean bloquearAsientos(String vueloId, String clase, int cantidad, String reservaVueloId) {
        List<Asiento> asientosDisponibles = asientoRepository.findAll().stream()
            .filter(a -> a.getVuelo() != null && a.getVuelo().getVueloId().equals(vueloId))
            .filter(a -> clase.equals(a.getClase()))
            .filter(a -> "DISPONIBLE".equals(a.getEstado()))
            .limit(cantidad)
            .collect(Collectors.toList());
            
        if (asientosDisponibles.size() < cantidad) {
            return false; // No hay suficientes asientos disponibles
        }
        
        Reserva reserva = reservaRepository.findById(reservaVueloId).orElse(null);
        
        for (Asiento asiento : asientosDisponibles) {
            asiento.setEstado("BLOQUEADO");
            asiento.setReserva(reserva);
            asientoRepository.save(asiento);
        }
        
        return true;
    }
    
    /**
     * Confirmar asientos bloqueados (cambiar a OCUPADO)
     */
    public void confirmarAsientosBloqueados(String reservaVueloId) {
        List<Asiento> asientosBloqueados = asientoRepository.findAll().stream()
            .filter(a -> a.getReserva() != null && reservaVueloId.equals(a.getReserva().getReservaVueloId()))
            .filter(a -> "BLOQUEADO".equals(a.getEstado()))
            .collect(Collectors.toList());
            
        for (Asiento asiento : asientosBloqueados) {
            asiento.setEstado("OCUPADO");
            asientoRepository.save(asiento);
        }
    }
    
    /**
     * Liberar asientos (volver a DISPONIBLE)
     */
    public void liberarAsientos(String reservaVueloId) {
        List<Asiento> asientosReserva = asientoRepository.findAll().stream()
            .filter(a -> a.getReserva() != null && reservaVueloId.equals(a.getReserva().getReservaVueloId()))
            .collect(Collectors.toList());
            
        for (Asiento asiento : asientosReserva) {
            asiento.setEstado("DISPONIBLE");
            asiento.setReserva(null);
            asiento.setClientIdAsignado(null);
            asientoRepository.save(asiento);
        }
    }
    
    /**
     * Asignar asiento específico a un cliente (para operaciones de check-in)
     */
    public boolean asignarAsientoACliente(String vueloId, String numeroAsiento, String clientId, String reservaVueloId) {
        Optional<Asiento> asientoOpt = asientoRepository.findAll().stream()
            .filter(a -> a.getVuelo() != null && a.getVuelo().getVueloId().equals(vueloId))
            .filter(a -> a.getNumero().equals(numeroAsiento))
            .findFirst();
            
        if (!asientoOpt.isPresent()) {
            return false; // Asiento no encontrado
        }
        
        Asiento asiento = asientoOpt.get();
        
        // Validar que el asiento esté ocupado (reserva confirmada)
        if (!"OCUPADO".equals(asiento.getEstado())) {
            throw new IllegalStateException("El asiento debe estar ocupado para poder asignarse");
        }
        
        // Validar que pertenezca a la reserva correcta
        if (asiento.getReserva() == null || !reservaVueloId.equals(asiento.getReserva().getReservaVueloId())) {
            throw new IllegalStateException("El asiento no pertenece a la reserva especificada");
        }
        
        // Realizar asignación
        asiento.setEstado("ASIGNADO");
        asiento.setClientIdAsignado(clientId);
        asientoRepository.save(asiento);
        
        return true;
    }

    public List<AsientoDTO> listarAsientos() {
        return asientoRepository.findAll().stream()
                .map(asientoMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Listar todos los asientos de un vuelo
     */
    public List<AsientoDTO> listarAsientosPorVuelo(String vueloId) {
        return asientoRepository.findAll().stream()
            .filter(a -> a.getVuelo() != null && a.getVuelo().getVueloId().equals(vueloId))
            .map(asientoMapper::toDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtener estadísticas de ocupación de un vuelo
     */
    public String obtenerEstadisticasOcupacion(String vueloId) {
        List<Asiento> asientos = asientoRepository.findAll().stream()
            .filter(a -> a.getVuelo() != null && a.getVuelo().getVueloId().equals(vueloId))
            .collect(Collectors.toList());
            
        long disponibles = asientos.stream().filter(a -> "DISPONIBLE".equals(a.getEstado())).count();
        long bloqueados = asientos.stream().filter(a -> "BLOQUEADO".equals(a.getEstado())).count();
        long ocupados = asientos.stream().filter(a -> "OCUPADO".equals(a.getEstado())).count();
        long asignados = asientos.stream().filter(a -> "ASIGNADO".equals(a.getEstado())).count();
        
        return String.format("Total: %d, Disponibles: %d, Bloqueados: %d, Ocupados: %d, Asignados: %d", 
                            asientos.size(), disponibles, bloqueados, ocupados, asignados);
    }

}