package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Aeropuerto {

    @Id
    private String codigoIATA; // Ej: "BOG", "MDE"

    private String nombre; // Ej: "Aeropuerto El Dorado"

    private String ciudad;

    private String pais;

    private String codigoICAO; // Código internacional de 4 letras

    @OneToMany(mappedBy = "origen")
    private List<Vuelo> vuelosOrigen;

    @OneToMany(mappedBy = "destino")
    private List<Vuelo> vuelosDestino;
}