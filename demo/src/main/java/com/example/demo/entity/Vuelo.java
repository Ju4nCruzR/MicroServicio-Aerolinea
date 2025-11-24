package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vuelo {

    @Id
    private UUID vueloId; // UUID v4 según especificaciones del ecosistema

    private String aerolinea; // Ej: "Avianca"

    private LocalDateTime fechaSalida; // Formato ISO 8601

    private LocalDateTime fechaLlegada; // Formato ISO 8601

    private String duracion; // Ej: "1h15m"

    private Double precio; // Precio por pasajero

    private String moneda; // Ej: "COP"

    private String clase; // "ECONOMICA", "EJECUTIVA", "PRIMERA_CLASE"

    private Integer disponibilidad; // Número de asientos disponibles

    private Integer capacidadTotal; // Capacidad total del vuelo

    private String estado; // "PROGRAMADO", "EN_VUELO", "COMPLETADO", "CANCELADO"

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_origen", nullable = false)
    private Aeropuerto origen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_destino", nullable = false)
    private Aeropuerto destino;

    @OneToMany(mappedBy = "vuelo")
    private List<Reserva> reservas;
}