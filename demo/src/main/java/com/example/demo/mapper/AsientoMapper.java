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
        dto.setIdAsiento(asiento.getAsientoId());
        dto.setNumero(asiento.getNumero());
        dto.setClase(asiento.getClase());
        dto.setDisponible(asiento.getDisponible());
        return dto;
    }

    public Asiento toEntity(AsientoDTO dto) {
        if (dto == null) {
            return null;
        }

        Asiento asiento = new Asiento();
        asiento.setAsientoId(dto.getIdAsiento());
        asiento.setNumero(dto.getNumero());
        asiento.setClase(dto.getClase());
        asiento.setDisponible(dto.isDisponible());
        return asiento;
    }
}