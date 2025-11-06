package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vuelo {

    @Id
    private String vueloId; // Ej: "AV123"

    private String aerolinea; // Ej: "Avianca"

    private String codigoOrigen; // Código IATA del aeropuerto origen (Ej: "BOG")

    private String codigoDestino; // Código IATA del aeropuerto destino (Ej: "MDE")

    private LocalDateTime fechaSalida; // Formato: "20251110T090000"

    private LocalDateTime fechaLlegada; // Formato: "20251110T101500"

    private String duracion; // Ej: "1h15m"

    private Double precio; // Precio por pasajero

    private String moneda; // Ej: "COP"

    private String clase; // Enum: "ECONOMICA", "EJECUTIVA", "PRIMERA_CLASE"

    private Integer disponibilidad; // Número de asientos disponibles

    private String estado; // Enum: "PROGRAMADO", "EN_VUELO", "COMPLETADO", "CANCELADO"

    @ManyToOne
    @JoinColumn(name = "codigo_origen", referencedColumnName = "codigoIATA")
    private Aeropuerto origen;

    @ManyToOne
    @JoinColumn(name = "codigo_destino", referencedColumnName = "codigoIATA")
    private Aeropuerto destino;

    @OneToMany(mappedBy = "vuelo")
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "vuelo")
    private List<Asiento> asientos;
}