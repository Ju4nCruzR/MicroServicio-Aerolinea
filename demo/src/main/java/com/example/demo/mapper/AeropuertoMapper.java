package com.example.demo.mapper;

import com.example.demo.dto.AeropuertoDTO;
import com.example.demo.entity.Aeropuerto;
import org.springframework.stereotype.Component;

@Component
public class AeropuertoMapper {

    public AeropuertoDTO toDTO(Aeropuerto aeropuerto) {
        if (aeropuerto == null) {
            return null;
        }

        AeropuertoDTO dto = new AeropuertoDTO();
        dto.setIdAeropuerto(aeropuerto.getCodigoIATA());
        dto.setNombre(aeropuerto.getNombre());
        dto.setCiudad(aeropuerto.getCiudad());
        dto.setPais(aeropuerto.getPais());
        return dto;
    }

    public Aeropuerto toEntity(AeropuertoDTO dto) {
        if (dto == null) {
            return null;
        }

        Aeropuerto aeropuerto = new Aeropuerto();
        aeropuerto.setCodigoIATA(dto.getIdAeropuerto());
        aeropuerto.setNombre(dto.getNombre());
        aeropuerto.setCiudad(dto.getCiudad());
        aeropuerto.setPais(dto.getPais());
        return aeropuerto;
    }
}