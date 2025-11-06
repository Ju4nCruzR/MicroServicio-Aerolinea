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
public class HistorialVuelos {

    @Id
    private String consultaId; // Ej: "CON12345"

    private String origen; // Código IATA

    private String destino; // Código IATA

    private String fechaSalida; // Formato: "20251110"

    private String fechaRegreso; // Formato: "20251120" (puede ser null para vuelos sencillos)

    private Integer numPasajeros;

    private String clase;

    private LocalDateTime fechaConsulta; // Timestamp de cuando se realizó la consulta

    @ManyToMany
    @JoinTable(name = "historial_vuelos_vuelo", joinColumns = @JoinColumn(name = "consulta_id"), inverseJoinColumns = @JoinColumn(name = "vuelo_id"))
    private List<Vuelo> vuelos;
}