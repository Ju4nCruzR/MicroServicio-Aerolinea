package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaVuelosResponseDTO {

    private String consultaId; // UUID v4 para trackear la consulta

    private List<VueloDTO> vuelos; // Lista de vuelos encontrados
}