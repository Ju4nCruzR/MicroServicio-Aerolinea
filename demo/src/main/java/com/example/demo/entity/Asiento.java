package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asiento {

    @Id
    private String asientoId;

    @ManyToOne
    @JoinColumn(name = "vuelo_id")
    private Vuelo vuelo;

    private String numero; // Ej: "12A", "15F"

    private String clase; // "ECONOMICA", "EJECUTIVA", "PRIMERA_CLASE"

    private Boolean disponible;

    private Double precio; // puede variar por asiento

    @ManyToOne
    @JoinColumn(name = "reserva_vuelo_id")
    private Reserva reserva;

}