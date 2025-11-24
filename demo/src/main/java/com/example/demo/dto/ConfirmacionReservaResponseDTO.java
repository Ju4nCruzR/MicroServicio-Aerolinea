package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmacionReservaResponseDTO {

    private String reservaVueloId; // ID de la reserva
    private String pnr; // Código PNR generado
    private String estadoFinal; // Estado final de la reserva
    private String transaccionBancariaId; // ID de transacción bancaria
    private String observaciones; // Mensajes adicionales
}