package com.example.demo.mapper;

import com.example.demo.dto.PasajeroDTO;
import com.example.demo.entity.Pasajero;
import org.springframework.stereotype.Component;

@Component
public class PasajeroMapper {

    public PasajeroDTO toDTO(Pasajero pasajero) {
        if (pasajero == null) {
            return null;
        }

        PasajeroDTO dto = new PasajeroDTO();
        dto.setIdPasajero(pasajero.getPasajeroId());
        dto.setNombre(pasajero.getNombre());
        dto.setEmail(pasajero.getEmail());
        dto.setDocumento(pasajero.getNumeroDocumento());
        return dto;
    }

    public Pasajero toEntity(PasajeroDTO dto) {
        if (dto == null) {
            return null;
        }

        Pasajero pasajero = new Pasajero();
        pasajero.setPasajeroId(dto.getIdPasajero());
        pasajero.setNombre(dto.getNombre());
        pasajero.setEmail(dto.getEmail());
        pasajero.setTipoDocumento(dto.getDocumento());
        return pasajero;
    }
}