package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {

    private String reservaVueloId; // ID alfanumérico de la pre-reserva
    private String reservaConfirmadaId; // PNR generado al confirmar (puede ser null)
    private String vueloId; // UUID v4 del vuelo
    private Integer numPasajeros; // Número de pasajeros en la reserva
    private String contactoReserva; // Nombre del contacto
    private String documentoContacto; // Documento de identidad del contacto
    private Double precioTotal; // Precio total de la reserva
    private String estado; // "PENDIENTE", "CONFIRMADA", "CANCELADA", "EXPIRADA"
    private LocalDateTime fechaCreacion; // Fecha de creación de la reserva
    private LocalDateTime fechaExpiracion; // Fecha límite para confirmar
    private LocalDateTime fechaConfirmacion; // Fecha de confirmación (puede ser null)
    private LocalDateTime fechaCancelacion; // Fecha de cancelación (puede ser null)
    private String transaccionId; // ID de transacción del Banco (puede ser null)
    private String observaciones; // Información adicional
    private String urlComprobante; // URL del comprobante PDF (puede ser null)
}