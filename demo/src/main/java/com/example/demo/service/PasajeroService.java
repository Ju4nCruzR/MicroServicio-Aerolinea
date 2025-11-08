package com.example.demo.service;

import com.example.demo.entity.Pasajero;
import com.example.demo.dto.PasajeroDTO;
import com.example.demo.mapper.PasajeroMapper;
import com.example.demo.repository.PasajeroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PasajeroService {

    @Autowired
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private PasajeroMapper pasajeroMapper;

    public PasajeroDTO crearPasajero(Pasajero pasajero) {
        // Validar que el email sea único
        boolean existeEmail = pasajeroRepository.findAll().stream()
                .anyMatch(p -> p.getEmail().equals(pasajero.getEmail()));
        if (existeEmail) {
            throw new IllegalArgumentException("Ya existe un pasajero con este email");
        }

        // Validar que el documento sea único
        boolean existeDocumento = pasajeroRepository.findAll().stream()
                .anyMatch(p -> p.getTipoDocumento().equals(pasajero.getTipoDocumento()));
        if (existeDocumento) {
            throw new IllegalArgumentException("Ya existe un pasajero con este documento");
        }

        Pasajero savedPasajero = pasajeroRepository.save(pasajero);
        return pasajeroMapper.toDTO(savedPasajero);
    }

    public PasajeroDTO consultarPasajero(String idPasajero) {
        Pasajero pasajero = pasajeroRepository.findById(idPasajero).orElse(null);
        return pasajero != null ? pasajeroMapper.toDTO(pasajero) : null;
    }

    public PasajeroDTO actualizarPasajero(Pasajero pasajero) {
        // Validar que el email sea único (excluyendo el pasajero actual)
        boolean existeEmail = pasajeroRepository.findAll().stream()
                .anyMatch(p -> !p.getPasajeroId().equals(pasajero.getPasajeroId()) &&
                        p.getEmail().equals(pasajero.getEmail()));
        if (existeEmail) {
            throw new IllegalArgumentException("Ya existe otro pasajero con este email");
        }

        // Validar que el documento sea único (excluyendo el pasajero actual)
        boolean existeDocumento = pasajeroRepository.findAll().stream()
                .anyMatch(p -> !p.getPasajeroId().equals(pasajero.getPasajeroId()) &&
                        p.getPasajeroId().equals(pasajero.getPasajeroId()));
        if (existeDocumento) {
            throw new IllegalArgumentException("Ya existe otro pasajero con este documento");
        }

        Pasajero savedPasajero = pasajeroRepository.save(pasajero);
        return pasajeroMapper.toDTO(savedPasajero);
    }

    public void eliminarPasajero(String idPasajero) {
        pasajeroRepository.deleteById(idPasajero);
    }

    public List<PasajeroDTO> listarPasajeros() {
        return pasajeroRepository.findAll().stream()
                .map(pasajeroMapper::toDTO)
                .collect(Collectors.toList());
    }

}