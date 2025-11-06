package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {

    private String idReserva;
    private LocalDateTime fechaReserva;
    private String estado;
    private String idVuelo;
    private String idPasajero;
    private String idAsiento;
}