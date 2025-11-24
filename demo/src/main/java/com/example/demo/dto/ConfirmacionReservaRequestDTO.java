package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmacionReservaRequestDTO {

    @NotBlank(message = "El ID de transacción bancaria es requerido")
    private String transaccionBancariaId; // ID de la transacción del banco
    
    @NotBlank(message = "El método de pago es requerido")
    private String metodoPago; // "TARJETA_CREDITO", "TARJETA_DEBITO", "PSE", etc.
}