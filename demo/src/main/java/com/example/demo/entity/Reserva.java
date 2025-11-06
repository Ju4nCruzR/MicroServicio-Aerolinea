package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    private String reservaVueloId; // Ej: "RSV98765"

    private String reservaConfirmadaId; // Ej: "PNR45678" (se genera al confirmar, puede ser null)

    @ManyToOne
    @JoinColumn(name = "vuelo_id")
    private Vuelo vuelo;

    private Integer numPasajeros;

    private String contactoReserva; // Nombre del contacto

    private String documentoContacto; // Cédula/documento

    private Double precioTotal;

    private String estado; // Enum: "PENDIENTE", "CONFIRMADA", "CANCELADA", "EXPIRADA"

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaExpiracion; // 30 minutos después de la creación

    private LocalDateTime fechaConfirmacion; // puede ser null

    private LocalDateTime fechaCancelacion; // puede ser null

    private String transaccionId; // Ej: "TX777888" (se asigna al confirmar)

    private String observaciones;

    private String urlComprobante; // URL del comprobante PDF (se genera al confirmar)

    @OneToMany(mappedBy = "reserva")
    private List<Pasajero> pasajeros;

    @OneToMany(mappedBy = "reserva")
    private List<Asiento> asientos;

}