package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VueloDTO {

    private String idVuelo;
    private String codigo;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;
    private String estado;
    private int capacidad;
    private String idAeropuertoOrigen;
    private String idAeropuertoDestino;
}