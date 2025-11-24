package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusquedaVuelosRequestDTO {

    @NotBlank(message = "El código IATA de origen es obligatorio")
    @Pattern(regexp = "^[A-Z]{3}$", message = "El código de origen debe ser un código IATA válido de 3 letras")
    private String origen; // Código IATA (ej: "BOG")

    @NotBlank(message = "El código IATA de destino es obligatorio")
    @Pattern(regexp = "^[A-Z]{3}$", message = "El código de destino debe ser un código IATA válido de 3 letras")
    private String destino; // Código IATA (ej: "MDE")

    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDate fechaSalida; // Formato ISO 8601

    private LocalDate fechaRegreso; // Opcional, para vuelos de ida y vuelta

    @NotNull(message = "El número de pasajeros es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 pasajero")
    private Integer numPasajeros;

    @NotBlank(message = "La clase es obligatoria")
    @Pattern(regexp = "^(ECONOMICA|EJECUTIVA|PRIMERA_CLASE)$", message = "La clase debe ser ECONOMICA, EJECUTIVA o PRIMERA_CLASE")
    private String clase;
}