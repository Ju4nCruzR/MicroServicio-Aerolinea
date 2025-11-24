package com.example.demo.mapper;

import com.example.demo.dto.AsientoDTO;
import com.example.demo.entity.Asiento;
import org.springframework.stereotype.Component;

@Component
public class AsientoMapper {

    public AsientoDTO toDTO(Asiento asiento) {
        if (asiento == null) {
            return null;
        }

        AsientoDTO dto = new AsientoDTO();
        dto.setNumero(asiento.getNumero());
        dto.setClase(asiento.getClase());
        dto.setEstado(asiento.getEstado());
        dto.setClientIdAsignado(asiento.getClientIdAsignado());
        
        // Mapear IDs relacionados
        if (asiento.getVuelo() != null) {
            dto.setVueloId(asiento.getVuelo().getVueloId());
        }
        if (asiento.getReserva() != null) {
            dto.setReservaVueloId(asiento.getReserva().getReservaVueloId());
        }
        return dto;
    }

    public Asiento toEntity(AsientoDTO dto) {
        if (dto == null) {
            return null;
        }

        Asiento asiento = new Asiento();
        asiento.setNumero(dto.getNumero());
        asiento.setClase(dto.getClase());
        asiento.setEstado(dto.getEstado());
        asiento.setClientIdAsignado(dto.getClientIdAsignado());
        // Nota: Las relaciones con Vuelo y Reserva deben ser manejadas en el servicio
        return asiento;
    }
}