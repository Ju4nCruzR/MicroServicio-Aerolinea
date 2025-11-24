package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreReservaRequestDTO {

    @NotBlank(message = "El ID del vuelo es obligatorio")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$", 
             message = "El vueloId debe ser un UUID v4 válido")
    private String vueloId; // UUID v4 del vuelo

    @NotNull(message = "El número de pasajeros es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 pasajero")
    private Integer numPasajeros;

    @NotBlank(message = "El contacto de reserva es obligatorio")
    private String contactoReserva; // Nombre del contacto

    @NotBlank(message = "El documento de contacto es obligatorio")
    private String documentoContacto; // Documento de identidad del contacto
}