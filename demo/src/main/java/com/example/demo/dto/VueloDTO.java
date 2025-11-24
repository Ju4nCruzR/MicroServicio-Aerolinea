package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VueloDTO {

    private UUID id; // UUID v4 del vuelo
    private String origen; // Código IATA del aeropuerto origen (ej: "BOG")
    private String destino; // Código IATA del aeropuerto destino (ej: "MDE")
    private LocalDateTime fechaSalida; // Formato ISO 8601
    private LocalDateTime fechaLlegada; // Formato ISO 8601
    private String clase; // "ECONOMICA", "EJECUTIVA", "PRIMERA_CLASE"
    private Double precio; // Precio por pasajero
    private Integer asientosDisponibles; // Para respuesta de búsqueda
    private String estado; // "PROGRAMADO", "EN_VUELO", "COMPLETADO", "CANCELADO"
    private String aerolinea; // Nombre de la aerolínea
    private String duracion; // Duración del vuelo (ej: "1h15m")
    private String moneda; // Moneda del precio (ej: "COP")
}