package com.example.demo.service;

import com.example.demo.entity.Asiento;
import com.example.demo.dto.AsientoDTO;
import com.example.demo.mapper.AsientoMapper;
import com.example.demo.repository.AsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AsientoService {

    @Autowired
    private AsientoRepository asientoRepository;

    @Autowired
    private AsientoMapper asientoMapper;

    public AsientoDTO crearAsiento(Asiento asiento) {
        Asiento savedAsiento = asientoRepository.save(asiento);
        return asientoMapper.toDTO(savedAsiento);
    }

    public AsientoDTO consultarAsiento(String idAsiento) {
        Asiento asiento = asientoRepository.findById(idAsiento).orElse(null);
        return asiento != null ? asientoMapper.toDTO(asiento) : null;
    }

    public AsientoDTO actualizarAsiento(Asiento asiento) {
        Asiento savedAsiento = asientoRepository.save(asiento);
        return asientoMapper.toDTO(savedAsiento);
    }

    public void eliminarAsiento(String idAsiento) {
        asientoRepository.deleteById(idAsiento);
    }

    public List<AsientoDTO> listarAsientos() {
        return asientoRepository.findAll().stream()
                .map(asientoMapper::toDTO)
                .collect(Collectors.toList());
    }

}