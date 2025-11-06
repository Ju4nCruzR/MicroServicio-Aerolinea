package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AeropuertoDTO {

    private String idAeropuerto;
    private String nombre;
    private String ciudad;
    private String pais;
}