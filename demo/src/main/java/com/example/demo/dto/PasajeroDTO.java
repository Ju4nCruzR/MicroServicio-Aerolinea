package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasajeroDTO {

    private String clientId; // ID alfanumérico enviado por el microservicio Turismo
    private String nombre; // Nombre completo del pasajero
    private String email; // Email del pasajero
    private String numeroDocumento; // Documento de identidad
}