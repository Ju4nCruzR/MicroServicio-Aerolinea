package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsientoDTO {

    private String numero; // Número del asiento en formato "12A"
    private String clase; // "ECONOMICA", "EJECUTIVA", "PRIMERA_CLASE"
    private String estado; // "DISPONIBLE", "BLOQUEADO", "OCUPADO", "ASIGNADO"
    private String vueloId; // UUID v4 del vuelo al que pertenece
    private String clientIdAsignado; // ID del cliente asignado (puede ser null)
    private String reservaVueloId; // ID de la reserva asociada (puede ser null)
}