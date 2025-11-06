package com.example.demo.mapper;

import com.example.demo.dto.ReservaDTO;
import com.example.demo.entity.Reserva;
import org.springframework.stereotype.Component;

@Component
public class ReservaMapper {

    public ReservaDTO toDTO(Reserva reserva) {
        if (reserva == null) {
            return null;
        }

        ReservaDTO dto = new ReservaDTO();
        dto.setIdReserva(reserva.getReservaVueloId());
        dto.setFechaReserva(reserva.getFechaCreacion());
        dto.setEstado(reserva.getEstado());
        if (reserva.getVuelo() != null) {
            dto.setIdVuelo(reserva.getVuelo().getVueloId());
        }
        if (reserva.getPasajeros() != null && !reserva.getPasajeros().isEmpty()) {
            dto.setIdPasajero(reserva.getPasajeros().get(0).getPasajeroId());
        }
        if (reserva.getAsientos() != null && !reserva.getAsientos().isEmpty()) {
            dto.setIdAsiento(reserva.getAsientos().get(0).getAsientoId());
        }
        return dto;
    }

    public Reserva toEntity(ReservaDTO dto) {
        if (dto == null) {
            return null;
        }

        Reserva reserva = new Reserva();
        reserva.setReservaVueloId(dto.getIdReserva());
        reserva.setFechaCreacion(dto.getFechaReserva());
        reserva.setEstado(dto.getEstado());
        // Nota: Las relaciones con Vuelo, Pasajero y Asiento deben ser manejadas en el
        // servicio
        return reserva;
    }
}