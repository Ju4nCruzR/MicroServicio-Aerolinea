package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionPasajeroResponseDTO {

    private String asignacionId; // ID único de la asignación operativa

    private String estado; // "ASIGNADO", "ERROR", etc.
}