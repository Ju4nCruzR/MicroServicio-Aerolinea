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
        dto.setClientId(pasajero.getClientId());
        dto.setNombre(pasajero.getNombre());
        dto.setEmail(pasajero.getEmail());
        dto.setNumeroDocumento(pasajero.getNumeroDocumento());
        
        return dto;
    }

    public Pasajero toEntity(PasajeroDTO dto) {
        if (dto == null) {
            return null;
        }

        Pasajero pasajero = new Pasajero();
        pasajero.setClientId(dto.getClientId());
        pasajero.setNombre(dto.getNombre());
        pasajero.setEmail(dto.getEmail());
        pasajero.setNumeroDocumento(dto.getNumeroDocumento());
        
        return pasajero;
    }
}