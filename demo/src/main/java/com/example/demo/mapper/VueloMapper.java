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
        dto.setIdVuelo(vuelo.getVueloId());
        dto.setCodigo(vuelo.getVueloId());
        dto.setFechaSalida(vuelo.getFechaSalida());
        dto.setFechaLlegada(vuelo.getFechaLlegada());
        dto.setEstado(vuelo.getEstado());
        dto.setCapacidad(vuelo.getDisponibilidad());
        if (vuelo.getOrigen() != null) {
            dto.setIdAeropuertoOrigen(vuelo.getOrigen().getCodigoIATA());
        }
        if (vuelo.getDestino() != null) {
            dto.setIdAeropuertoDestino(vuelo.getDestino().getCodigoIATA());
        }
        return dto;
    }

    public Vuelo toEntity(VueloDTO dto) {
        if (dto == null) {
            return null;
        }

        Vuelo vuelo = new Vuelo();
        vuelo.setVueloId(dto.getIdVuelo());
        vuelo.setVueloId(dto.getCodigo());
        vuelo.setFechaSalida(dto.getFechaSalida());
        vuelo.setFechaLlegada(dto.getFechaLlegada());
        vuelo.setEstado(dto.getEstado());
        vuelo.setDisponibilidad(dto.getCapacidad());
        // Nota: Las relaciones con Aeropuerto deben ser manejadas en el servicio
        return vuelo;
    }
}