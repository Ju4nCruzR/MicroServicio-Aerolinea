package com.example.demo.service;

import com.example.demo.entity.Reserva;
import com.example.demo.entity.Asiento;
import com.example.demo.dto.ReservaDTO;
import com.example.demo.mapper.ReservaMapper;
import com.example.demo.repository.ReservaRepository;
import com.example.demo.repository.VueloRepository;
import com.example.demo.repository.PasajeroRepository;
import com.example.demo.repository.AsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

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

    public ReservaDTO crearReserva(Reserva reserva) {
        // Validar que el asiento esté disponible
        if (reserva.getAsientos() != null && !reserva.getAsientos().isEmpty()
                && !reserva.getAsientos().get(0).getDisponible()) {
            throw new IllegalArgumentException("El asiento no está disponible");
        }

        // Validar que el pasajero no tenga ya una reserva para este vuelo
        boolean existeReserva = reservaRepository.findAll().stream()
                .anyMatch(r -> r.getVuelo().getVueloId().equals(reserva.getVuelo().getVueloId()) &&
                        !r.getPasajeros().isEmpty() && reserva.getPasajeros() != null
                        && !reserva.getPasajeros().isEmpty() &&
                        r.getPasajeros().get(0).getPasajeroId().equals(reserva.getPasajeros().get(0).getPasajeroId()) &&
                        !"CANCELADA".equals(r.getEstado()));
        if (existeReserva) {
            throw new IllegalArgumentException("El pasajero ya tiene una reserva activa para este vuelo");
        }

        reserva.setEstado("PENDIENTE");
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setFechaExpiracion(reserva.getFechaCreacion().plusMinutes(30));

        // Marcar asiento como no disponible
        if (reserva.getAsientos() != null && !reserva.getAsientos().isEmpty()) {
            reserva.getAsientos().get(0).setDisponible(false);
            asientoRepository.save(reserva.getAsientos().get(0));
        }

        Reserva savedReserva = reservaRepository.save(reserva);
        return reservaMapper.toDTO(savedReserva);
    }

    public ReservaDTO consultarReserva(String idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva).orElse(null);
        return reservaMapper.toDTO(reserva);
    }

    public ReservaDTO cancelarReserva(String idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva).orElse(null);
        if (reserva != null) {
            reserva.setEstado("CANCELADA");
            reserva.setFechaCancelacion(LocalDateTime.now());

            // Liberar el asiento
            if (reserva.getAsientos() != null && !reserva.getAsientos().isEmpty()) {
                reserva.getAsientos().get(0).setDisponible(true);
                asientoRepository.save(reserva.getAsientos().get(0));
            }

            Reserva savedReserva = reservaRepository.save(reserva);
            return reservaMapper.toDTO(savedReserva);
        }
        return null;
    }

    public ReservaDTO confirmarReserva(String idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva).orElse(null);
        if (reserva != null) {
            reserva.setEstado("CONFIRMADA");
            reserva.setFechaConfirmacion(LocalDateTime.now());
            Reserva savedReserva = reservaRepository.save(reserva);
            return reservaMapper.toDTO(savedReserva);
        }
        return null;
    }

    public List<ReservaDTO> listarReservas() {
        return reservaRepository.findAll().stream()
                .map(reservaMapper::toDTO)
                .collect(Collectors.toList());
    }

}