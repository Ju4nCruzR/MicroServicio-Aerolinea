package com.example.demo.mapper;

import com.example.demo.dto.VueloDTO;
import com.example.demo.entity.Vuelo;
import org.springframework.stereotype.Component;

@Component
public class VueloMapper {

    public VueloDTO toDTO(Vuelo vuelo) {
        if (vuelo == null) {
            return null;
        }

        VueloDTO dto = new VueloDTO();
        dto.setId(vuelo.getVueloId());
        dto.setFechaSalida(vuelo.getFechaSalida());
        dto.setFechaLlegada(vuelo.getFechaLlegada());
        dto.setClase(vuelo.getClase());
        dto.setPrecio(vuelo.getPrecio());
        dto.setAsientosDisponibles(vuelo.getDisponibilidad());
        dto.setEstado(vuelo.getEstado());
        dto.setAerolinea(vuelo.getAerolinea());
        dto.setDuracion(vuelo.getDuracion());
        dto.setMoneda(vuelo.getMoneda());
        
        // Mapear códigos IATA de aeropuertos
        if (vuelo.getOrigen() != null) {
            dto.setOrigen(vuelo.getOrigen().getCodigoIATA());
        }
        if (vuelo.getDestino() != null) {
            dto.setDestino(vuelo.getDestino().getCodigoIATA());
        }
        return dto;
    }

    public Vuelo toEntity(VueloDTO dto) {
        if (dto == null) {
            return null;
        }

        Vuelo vuelo = new Vuelo();
        vuelo.setVueloId(dto.getId());
        vuelo.setFechaSalida(dto.getFechaSalida());
        vuelo.setFechaLlegada(dto.getFechaLlegada());
        vuelo.setClase(dto.getClase());
        vuelo.setPrecio(dto.getPrecio());
        vuelo.setDisponibilidad(dto.getAsientosDisponibles());
        vuelo.setEstado(dto.getEstado());
        vuelo.setAerolinea(dto.getAerolinea());
        vuelo.setDuracion(dto.getDuracion());
        vuelo.setMoneda(dto.getMoneda());
        // Nota: Las relaciones con Aeropuerto deben ser manejadas en el servicio
        return vuelo;
    }
}