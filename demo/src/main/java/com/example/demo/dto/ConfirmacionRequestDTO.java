package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.DecimalMin;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmacionRequestDTO {

    @NotBlank(message = "El ID de la reserva de vuelo es obligatorio")
    private String reservaVueloId; // ID alfanumérico de la pre-reserva

    @NotBlank(message = "El ID de transacción es obligatorio")
    private String transaccionId; // ID de transacción autorizada por Banco

    @NotNull(message = "El precio total confirmado es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private Double precioTotalConfirmado; // Debe coincidir con el precio original

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^(CONFIRMADO|DENEGADO)$", message = "El estado debe ser CONFIRMADO o DENEGADO")
    private String estado; // "CONFIRMADO" o "DENEGADO"
}