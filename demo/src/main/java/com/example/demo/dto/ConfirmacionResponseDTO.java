package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmacionResponseDTO {

    private String reservaConfirmadaId; // PNR generado (solo si se confirmó)

    private String estadoFinal; // "CONFIRMADA", "CANCELADA"

    private Double precioTotalConfirmado; // Precio final de la operación
}