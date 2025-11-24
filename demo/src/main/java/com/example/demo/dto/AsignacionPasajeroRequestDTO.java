package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionPasajeroRequestDTO {

    @NotBlank(message = "El Flight ID es obligatorio")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$", 
             message = "El flightId debe ser un UUID v4 válido")
    private String flightId; // UUID v4 del vuelo (según especificaciones del ecosistema)

    @NotBlank(message = "El Client ID es obligatorio")
    private String clientId; // ID alfanumérico del cliente enviado por Turismo

    @NotBlank(message = "El Reservation ID es obligatorio")
    private String reservationId; // ID alfanumérico de la reserva enviado por Turismo

    @NotBlank(message = "El asiento es obligatorio")
    @Pattern(regexp = "^[1-9][0-9]*[A-Z]$", message = "El asiento debe tener formato válido (ej: 12A)")
    private String asiento; // Formato: número + letra (ej: "12A")
}