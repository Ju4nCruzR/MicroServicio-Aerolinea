package com.example.demo.service;

import com.example.demo.entity.Aeropuerto;
import com.example.demo.dto.AeropuertoDTO;
import com.example.demo.mapper.AeropuertoMapper;
import com.example.demo.repository.AeropuertoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AeropuertoService {

    @Autowired
    private AeropuertoRepository aeropuertoRepository;

    @Autowired
    private AeropuertoMapper aeropuertoMapper;

    public AeropuertoDTO crearAeropuerto(Aeropuerto aeropuerto) {
        Aeropuerto savedAeropuerto = aeropuertoRepository.save(aeropuerto);
        return aeropuertoMapper.toDTO(savedAeropuerto);
    }

    public AeropuertoDTO consultarAeropuerto(String idAeropuerto) {
        Aeropuerto aeropuerto = aeropuertoRepository.findById(idAeropuerto).orElse(null);
        return aeropuerto != null ? aeropuertoMapper.toDTO(aeropuerto) : null;
    }

    public AeropuertoDTO actualizarAeropuerto(Aeropuerto aeropuerto) {
        Aeropuerto savedAeropuerto = aeropuertoRepository.save(aeropuerto);
        return aeropuertoMapper.toDTO(savedAeropuerto);
    }

    public void eliminarAeropuerto(String idAeropuerto) {
        aeropuertoRepository.deleteById(idAeropuerto);
    }

    public List<AeropuertoDTO> listarAeropuertos() {
        return aeropuertoRepository.findAll().stream()
                .map(aeropuertoMapper::toDTO)
                .collect(Collectors.toList());
    }

}