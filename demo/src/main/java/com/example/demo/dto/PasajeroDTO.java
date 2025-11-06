package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasajeroDTO {

    private String idPasajero;
    private String nombre;
    private String email;
    private String documento;
}