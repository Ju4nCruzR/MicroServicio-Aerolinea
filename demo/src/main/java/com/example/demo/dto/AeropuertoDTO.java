package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AeropuertoDTO {

    private String codigoIATA; // Código IATA como identificador principal (ej: "BOG", "MDE")
    private String nombre; // Nombre del aeropuerto
    private String ciudad; // Ciudad donde se ubica
    private String pais; // País donde se ubica
    private String codigoICAO; // Código ICAO de 4 letras (opcional)
}