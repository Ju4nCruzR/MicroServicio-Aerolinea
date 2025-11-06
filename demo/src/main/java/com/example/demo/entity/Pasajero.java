package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pasajero {

    @Id
    private String pasajeroId;

    private String nombre;

    private String apellido;

    private String tipoDocumento; // Ej: "CC", "TI", "PASAPORTE"

    @Column(unique = true)
    private String numeroDocumento; // único

    private String email;

    private String telefono;

    private LocalDate fechaNacimiento;

    private String nacionalidad;

    @ManyToOne
    @JoinColumn(name = "reserva_vuelo_id")
    private Reserva reserva;

    // @OneToMany(mappedBy = "pasajero")
    // private List<Reserva> reservas;

}