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
        dto.setReservaVueloId(reserva.getReservaVueloId());
        dto.setReservaConfirmadaId(reserva.getReservaConfirmadaId());
        dto.setNumPasajeros(reserva.getNumPasajeros());
        dto.setContactoReserva(reserva.getContactoReserva());
        dto.setDocumentoContacto(reserva.getDocumentoContacto());
        dto.setPrecioTotal(reserva.getPrecioTotal());
        dto.setEstado(reserva.getEstado());
        dto.setFechaCreacion(reserva.getFechaCreacion());
        dto.setFechaExpiracion(reserva.getFechaExpiracion());
        dto.setFechaConfirmacion(reserva.getFechaConfirmacion());
        dto.setFechaCancelacion(reserva.getFechaCancelacion());
        dto.setTransaccionId(reserva.getTransaccionId());
        dto.setObservaciones(reserva.getObservaciones());
        dto.setUrlComprobante(reserva.getUrlComprobante());
        
        if (reserva.getVuelo() != null) {
            dto.setVueloId(reserva.getVuelo().getVueloId());
        }
        return dto;
    }

    public Reserva toEntity(ReservaDTO dto) {
        if (dto == null) {
            return null;
        }

        Reserva reserva = new Reserva();
        reserva.setReservaVueloId(dto.getReservaVueloId());
        reserva.setReservaConfirmadaId(dto.getReservaConfirmadaId());
        reserva.setNumPasajeros(dto.getNumPasajeros());
        reserva.setContactoReserva(dto.getContactoReserva());
        reserva.setDocumentoContacto(dto.getDocumentoContacto());
        reserva.setPrecioTotal(dto.getPrecioTotal());
        reserva.setEstado(dto.getEstado());
        reserva.setFechaCreacion(dto.getFechaCreacion());
        reserva.setFechaExpiracion(dto.getFechaExpiracion());
        reserva.setFechaConfirmacion(dto.getFechaConfirmacion());
        reserva.setFechaCancelacion(dto.getFechaCancelacion());
        reserva.setTransaccionId(dto.getTransaccionId());
        reserva.setObservaciones(dto.getObservaciones());
        reserva.setUrlComprobante(dto.getUrlComprobante());
        // Nota: Las relaciones con Vuelo deben ser manejadas en el servicio
        return reserva;
    }
}