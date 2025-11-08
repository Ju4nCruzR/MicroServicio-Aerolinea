package com.example.demo.service;

import com.example.demo.entity.Vuelo;
import com.example.demo.dto.VueloDTO;
import com.example.demo.mapper.VueloMapper;
import com.example.demo.repository.VueloRepository;
import com.example.demo.repository.AeropuertoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VueloService {

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private AeropuertoRepository aeropuertoRepository;

    @Autowired
    private VueloMapper vueloMapper;

    public VueloDTO crearVuelo(Vuelo vuelo) {
        // Validar fechas coherentes
        if (vuelo.getFechaSalida() != null && vuelo.getFechaLlegada() != null &&
                vuelo.getFechaSalida().isAfter(vuelo.getFechaLlegada())) {
            throw new IllegalArgumentException("La fecha de salida no puede ser posterior a la fecha de llegada");
        }

        // Validar que el código de vuelo sea único
        boolean existeCodigo = vueloRepository.findAll().stream()
                .anyMatch(v -> v.getVueloId().equals(vuelo.getVueloId()));
        if (existeCodigo) {
            throw new IllegalArgumentException("Ya existe un vuelo con este código");
        }

        vuelo.setEstado("PROGRAMADO");
        Vuelo savedVuelo = vueloRepository.save(vuelo);
        return vueloMapper.toDTO(savedVuelo);
    }

    public VueloDTO consultarVuelo(String idVuelo) {
        Vuelo vuelo = vueloRepository.findById(idVuelo).orElse(null);
        return vuelo != null ? vueloMapper.toDTO(vuelo) : null;
    }

    public VueloDTO actualizarVuelo(Vuelo vuelo) {
        // Validar fechas coherentes
        if (vuelo.getFechaSalida() != null && vuelo.getFechaLlegada() != null &&
                vuelo.getFechaSalida().isAfter(vuelo.getFechaLlegada())) {
            throw new IllegalArgumentException("La fecha de salida no puede ser posterior a la fecha de llegada");
        }

        // Lógica de actualización si es necesaria
        Vuelo savedVuelo = vueloRepository.save(vuelo);
        return vueloMapper.toDTO(savedVuelo);
    }

    public void eliminarVuelo(String idVuelo) {
        Vuelo vuelo = vueloRepository.findById(idVuelo).orElse(null);
        if (vuelo != null) {
            // Lógica de eliminación si es necesaria
            vueloRepository.delete(vuelo);
        }
    }

    public List<VueloDTO> listarVuelos() {
        return vueloRepository.findAll().stream()
                .map(vueloMapper::toDTO)
                .collect(Collectors.toList());
    }

}