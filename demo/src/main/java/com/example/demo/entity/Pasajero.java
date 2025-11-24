package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pasajero {

    @Id
    private String clientId; // ID alfanumérico del ecosistema Turismo

    private String nombre; // Nombre completo del pasajero

    private String email;

    @Column(unique = true)
    private String numeroDocumento; // Documento de identidad único

}