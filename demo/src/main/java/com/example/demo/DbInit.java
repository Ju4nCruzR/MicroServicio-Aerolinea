package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;


@Component
public class DbInit implements CommandLineRunner {

    @Autowired
    private AeropuertoRepository aeropuertoRepository;

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear aeropuertos
        Aeropuerto bog = new Aeropuerto();
        bog.setCodigoIATA("BOG");
        bog.setNombre("Aeropuerto El Dorado");
        bog.setCiudad("Bogotá");
        bog.setPais("Colombia");
        bog.setCodigoICAO("SKBO");
        aeropuertoRepository.save(bog);

        Aeropuerto mde = new Aeropuerto();
        mde.setCodigoIATA("MDE");
        mde.setNombre("Aeropuerto José María Córdova");
        mde.setCiudad("Medellín");
        mde.setPais("Colombia");
        mde.setCodigoICAO("SKRG");
        aeropuertoRepository.save(mde);

        Aeropuerto baq = new Aeropuerto();
        baq.setCodigoIATA("BAQ");
        baq.setNombre("Aeropuerto Ernesto Cortissoz");
        baq.setCiudad("Barranquilla");
        baq.setPais("Colombia");
        baq.setCodigoICAO("SKBQ");
        aeropuertoRepository.save(baq);

        // Crear vuelos con UUID v4 - Datos para coincidir con pruebas HTTP
        // Vuelo BOG -> MDE para las pruebas de búsqueda (diciembre 1)
        UUID vuelo1UUID = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479"); // UUID fijo para pruebas
        Vuelo vuelo1 = new Vuelo();
        vuelo1.setVueloId(vuelo1UUID);
        vuelo1.setAerolinea("Avianca");
        vuelo1.setFechaSalida(LocalDateTime.of(2025, 12, 1, 8, 0));
        vuelo1.setFechaLlegada(LocalDateTime.of(2025, 12, 1, 9, 30));
        vuelo1.setDuracion("1h30m");
        vuelo1.setPrecio(250000.0);
        vuelo1.setMoneda("COP");
        vuelo1.setClase("ECONOMICA");
        vuelo1.setDisponibilidad(180);
        vuelo1.setCapacidadTotal(180);
        vuelo1.setEstado("PROGRAMADO");
        vuelo1.setOrigen(bog);
        vuelo1.setDestino(mde);
        vueloRepository.save(vuelo1);

        // Vuelo BOG -> MDE para las pruebas (diciembre 1, horario tarde)
        Vuelo vuelo2 = new Vuelo();
        vuelo2.setVueloId(UUID.randomUUID());
        vuelo2.setAerolinea("Avianca");
        vuelo2.setFechaSalida(LocalDateTime.of(2025, 12, 1, 14, 0));
        vuelo2.setFechaLlegada(LocalDateTime.of(2025, 12, 1, 15, 30));
        vuelo2.setDuracion("1h30m");
        vuelo2.setPrecio(280000.0);
        vuelo2.setMoneda("COP");
        vuelo2.setClase("ECONOMICA");
        vuelo2.setDisponibilidad(150);
        vuelo2.setCapacidadTotal(150);
        vuelo2.setEstado("PROGRAMADO");
        vuelo2.setOrigen(bog);
        vuelo2.setDestino(mde);
        vueloRepository.save(vuelo2);

        // Vuelo BOG -> BAQ para las pruebas de ida y vuelta (diciembre 1)
        Vuelo vuelo3 = new Vuelo();
        vuelo3.setVueloId(UUID.randomUUID());
        vuelo3.setAerolinea("Avianca");
        vuelo3.setFechaSalida(LocalDateTime.of(2025, 12, 1, 10, 0));
        vuelo3.setFechaLlegada(LocalDateTime.of(2025, 12, 1, 12, 0));
        vuelo3.setDuracion("2h00m");
        vuelo3.setPrecio(320000.0);
        vuelo3.setMoneda("COP");
        vuelo3.setClase("ECONOMICA");
        vuelo3.setDisponibilidad(200);
        vuelo3.setCapacidadTotal(200);
        vuelo3.setEstado("PROGRAMADO");
        vuelo3.setOrigen(bog);
        vuelo3.setDestino(baq);
        vueloRepository.save(vuelo3);

        // Vuelo BAQ -> BOG para vuelta (diciembre 5)
        Vuelo vuelo4 = new Vuelo();
        vuelo4.setVueloId(UUID.randomUUID());
        vuelo4.setAerolinea("Avianca");
        vuelo4.setFechaSalida(LocalDateTime.of(2025, 12, 5, 16, 0));
        vuelo4.setFechaLlegada(LocalDateTime.of(2025, 12, 5, 18, 0));
        vuelo4.setDuracion("2h00m");
        vuelo4.setPrecio(340000.0);
        vuelo4.setMoneda("COP");
        vuelo4.setClase("ECONOMICA");
        vuelo4.setDisponibilidad(180);
        vuelo4.setCapacidadTotal(180);
        vuelo4.setEstado("PROGRAMADO");
        vuelo4.setOrigen(baq);
        vuelo4.setDestino(bog);
        vueloRepository.save(vuelo4);

        // Vuelo adicional BOG -> MDE con poca disponibilidad para pruebas de error
        Vuelo vuelo5 = new Vuelo();
        vuelo5.setVueloId(UUID.randomUUID());
        vuelo5.setAerolinea("Avianca");
        vuelo5.setFechaSalida(LocalDateTime.of(2025, 12, 1, 18, 0));
        vuelo5.setFechaLlegada(LocalDateTime.of(2025, 12, 1, 19, 30));
        vuelo5.setDuracion("1h30m");
        vuelo5.setPrecio(300000.0);
        vuelo5.setMoneda("COP");
        vuelo5.setClase("ECONOMICA");
        vuelo5.setDisponibilidad(2); // Poca disponibilidad para probar errores
        vuelo5.setCapacidadTotal(180);
        vuelo5.setEstado("PROGRAMADO");
        vuelo5.setOrigen(bog);
        vuelo5.setDestino(mde);
        vueloRepository.save(vuelo5);

        // Asientos eliminados - se maneja solo por disponibilidad directa del vuelo

        // Crear pasajeros con clientId del ecosistema Turismo
        Pasajero pasajero1 = new Pasajero();
        pasajero1.setClientId("CLT001JUAN");
        pasajero1.setNombre("Juan Pérez");
        pasajero1.setEmail("juan.perez@email.com");
        pasajero1.setNumeroDocumento("12345678");
        pasajeroRepository.save(pasajero1);

        Pasajero pasajero2 = new Pasajero();
        pasajero2.setClientId("CLT002MARI");
        pasajero2.setNombre("María García");
        pasajero2.setEmail("maria.garcia@email.com");
        pasajero2.setNumeroDocumento("87654321");
        pasajeroRepository.save(pasajero2);

        Pasajero pasajero3 = new Pasajero();
        pasajero3.setClientId("CLT003CARL");
        pasajero3.setNombre("Carlos Rodríguez");
        pasajero3.setEmail("carlos.rodriguez@email.com");
        pasajero3.setNumeroDocumento("11223344");
        pasajeroRepository.save(pasajero3);

        Pasajero pasajero4 = new Pasajero();
        pasajero4.setClientId("CLT004ANAL");
        pasajero4.setNombre("Ana López");
        pasajero4.setEmail("ana.lopez@email.com");
        pasajero4.setNumeroDocumento("44332211");
        pasajeroRepository.save(pasajero4);

        Pasajero pasajero5 = new Pasajero();
        pasajero5.setClientId("CLT005PEDR");
        pasajero5.setNombre("Pedro Martínez");
        pasajero5.setEmail("pedro.martinez@email.com");
        pasajero5.setNumeroDocumento("55667788");
        pasajeroRepository.save(pasajero5);

        // Crear reservas con IDs alfanuméricos
        Reserva reserva1 = new Reserva();
        reserva1.setReservaVueloId("RSV001JUAN");
        reserva1.setReservaConfirmadaId("PNR001CONF");
        reserva1.setVuelo(vuelo1);
        reserva1.setNumPasajeros(1);
        reserva1.setContactoReserva("Juan Pérez");
        reserva1.setDocumentoContacto("12345678");
        reserva1.setPrecioTotal(250000.0);
        reserva1.setEstado("CONFIRMADA");
        reserva1.setFechaCreacion(LocalDateTime.now());
        reserva1.setFechaConfirmacion(LocalDateTime.now());
        reserva1.setTransaccionId("TX001BANC");
        reserva1.setObservaciones("Reserva confirmada");
        reserva1.setUrlComprobante("https://aerolinea.com/comprobantes/PNR001CONF.pdf");
        reservaRepository.save(reserva1);
        
        // Actualizar disponibilidad del vuelo directamente
        vuelo1.setDisponibilidad(vuelo1.getDisponibilidad() - reserva1.getNumPasajeros());
        vueloRepository.save(vuelo1);

        Reserva reserva2 = new Reserva();
        reserva2.setReservaVueloId("RSV002MARI");
        reserva2.setReservaConfirmadaId("PNR002CONF");
        reserva2.setVuelo(vuelo2);
        reserva2.setNumPasajeros(1);
        reserva2.setContactoReserva("María García");
        reserva2.setDocumentoContacto("87654321");
        reserva2.setPrecioTotal(200000.0);
        reserva2.setEstado("CONFIRMADA");
        reserva2.setFechaCreacion(LocalDateTime.now());
        reserva2.setFechaConfirmacion(LocalDateTime.now());
        reserva2.setTransaccionId("TX002BANC");
        reserva2.setObservaciones("Reserva confirmada");
        reserva2.setUrlComprobante("https://aerolinea.com/comprobantes/PNR002CONF.pdf");
        reservaRepository.save(reserva2);
        
        // Actualizar disponibilidad del vuelo directamente
        vuelo2.setDisponibilidad(vuelo2.getDisponibilidad() - reserva2.getNumPasajeros());
        vueloRepository.save(vuelo2);

        Reserva reserva3 = new Reserva();
        reserva3.setReservaVueloId("RSV003CARL");
        reserva3.setVuelo(vuelo3);
        reserva3.setNumPasajeros(1);
        reserva3.setContactoReserva("Carlos Rodríguez");
        reserva3.setDocumentoContacto("11223344");
        reserva3.setPrecioTotal(220000.0);
        reserva3.setEstado("PENDIENTE");
        reserva3.setFechaCreacion(LocalDateTime.now());
        reserva3.setFechaExpiracion(LocalDateTime.now().plusMinutes(30));
        reserva3.setObservaciones("Pre-reserva pendiente");
        reservaRepository.save(reserva3);
        
        // Bloquear disponibilidad temporalmente para reserva pendiente
        vuelo3.setDisponibilidad(vuelo3.getDisponibilidad() - reserva3.getNumPasajeros());
        vueloRepository.save(vuelo3);
    }
}