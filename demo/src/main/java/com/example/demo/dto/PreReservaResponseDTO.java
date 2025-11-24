package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreReservaResponseDTO {

    private String reservaVueloId; // ID alfanumérico de la pre-reserva

    private Double precioTotal; // Precio total de la reserva

    private String estadoInicial; // Siempre "PENDIENTE"

    private LocalDateTime fechaExpiracion; // Fecha límite para confirmar (30 min)

    private String observaciones; // Información adicional sobre la reserva
}