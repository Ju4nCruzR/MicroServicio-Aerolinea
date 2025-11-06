package com.example.demo;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DbInit implements CommandLineRunner {

    @Autowired
    private AeropuertoRepository aeropuertoRepository;

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private AsientoRepository asientoRepository;

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

        // Crear vuelos
        Vuelo vuelo1 = new Vuelo();
        vuelo1.setVueloId("AV001");
        vuelo1.setAerolinea("Avianca");
        // vuelo1.setCodigoOrigen("BOG");
        // vuelo1.setCodigoDestino("MDE");
        vuelo1.setFechaSalida(LocalDateTime.of(2025, 11, 7, 8, 0));
        vuelo1.setFechaLlegada(LocalDateTime.of(2025, 11, 7, 9, 30));
        vuelo1.setDuracion("1h30m");
        vuelo1.setPrecio(250000.0);
        vuelo1.setMoneda("COP");
        vuelo1.setClase("ECONOMICA");
        vuelo1.setDisponibilidad(10);
        vuelo1.setEstado("PROGRAMADO");
        vuelo1.setOrigen(bog);
        vuelo1.setDestino(mde);
        vueloRepository.save(vuelo1);

        Vuelo vuelo2 = new Vuelo();
        vuelo2.setVueloId("AV002");
        vuelo2.setAerolinea("Avianca");
        // vuelo2.setCodigoOrigen("MDE");
        // vuelo2.setCodigoDestino("BAQ");
        vuelo2.setFechaSalida(LocalDateTime.of(2025, 11, 7, 10, 0));
        vuelo2.setFechaLlegada(LocalDateTime.of(2025, 11, 7, 11, 30));
        vuelo2.setDuracion("1h30m");
        vuelo2.setPrecio(200000.0);
        vuelo2.setMoneda("COP");
        vuelo2.setClase("ECONOMICA");
        vuelo2.setDisponibilidad(10);
        vuelo2.setEstado("PROGRAMADO");
        vuelo2.setOrigen(mde);
        vuelo2.setDestino(baq);
        vueloRepository.save(vuelo2);

        Vuelo vuelo3 = new Vuelo();
        vuelo3.setVueloId("AV003");
        vuelo3.setAerolinea("Avianca");
        // vuelo3.setCodigoOrigen("BAQ");
        // vuelo3.setCodigoDestino("BOG");
        vuelo3.setFechaSalida(LocalDateTime.of(2025, 11, 7, 12, 0));
        vuelo3.setFechaLlegada(LocalDateTime.of(2025, 11, 7, 13, 30));
        vuelo3.setDuracion("1h30m");
        vuelo3.setPrecio(220000.0);
        vuelo3.setMoneda("COP");
        vuelo3.setClase("ECONOMICA");
        vuelo3.setDisponibilidad(10);
        vuelo3.setEstado("PROGRAMADO");
        vuelo3.setOrigen(baq);
        vuelo3.setDestino(bog);
        vueloRepository.save(vuelo3);

        Vuelo vuelo4 = new Vuelo();
        vuelo4.setVueloId("AV004");
        vuelo4.setAerolinea("Avianca");
        // vuelo4.setCodigoOrigen("BOG");
        // vuelo4.setCodigoDestino("BAQ");
        vuelo4.setFechaSalida(LocalDateTime.of(2025, 11, 8, 8, 0));
        vuelo4.setFechaLlegada(LocalDateTime.of(2025, 11, 8, 9, 30));
        vuelo4.setDuracion("1h30m");
        vuelo4.setPrecio(240000.0);
        vuelo4.setMoneda("COP");
        vuelo4.setClase("ECONOMICA");
        vuelo4.setDisponibilidad(10);
        vuelo4.setEstado("PROGRAMADO");
        vuelo4.setOrigen(bog);
        vuelo4.setDestino(baq);
        vueloRepository.save(vuelo4);

        Vuelo vuelo5 = new Vuelo();
        vuelo5.setVueloId("AV005");
        vuelo5.setAerolinea("Avianca");
        // vuelo5.setCodigoOrigen("BAQ");
        // vuelo5.setCodigoDestino("MDE");
        vuelo5.setFechaSalida(LocalDateTime.of(2025, 11, 8, 10, 0));
        vuelo5.setFechaLlegada(LocalDateTime.of(2025, 11, 8, 11, 30));
        vuelo5.setDuracion("1h30m");
        vuelo5.setPrecio(210000.0);
        vuelo5.setMoneda("COP");
        vuelo5.setClase("ECONOMICA");
        vuelo5.setDisponibilidad(10);
        vuelo5.setEstado("PROGRAMADO");
        vuelo5.setOrigen(baq);
        vuelo5.setDestino(mde);
        vueloRepository.save(vuelo5);

        // Crear asientos para cada vuelo
        List<Vuelo> vuelos = List.of(vuelo1, vuelo2, vuelo3, vuelo4, vuelo5);
        for (Vuelo vuelo : vuelos) {
            for (int i = 1; i <= 10; i++) {
                Asiento asiento = new Asiento();
                asiento.setAsientoId(vuelo.getVueloId() + "-" + String.format("%02d", i));
                asiento.setVuelo(vuelo);
                asiento.setNumero(String.format("%02d", i) + "A");
                asiento.setClase("ECONOMICA");
                asiento.setDisponible(true);
                asiento.setPrecio(vuelo.getPrecio());
                asientoRepository.save(asiento);
            }
        }

        // Crear pasajeros
        Pasajero pasajero1 = new Pasajero();
        pasajero1.setPasajeroId("P001");
        pasajero1.setNombre("Juan");
        pasajero1.setApellido("Pérez");
        pasajero1.setTipoDocumento("CC");
        pasajero1.setNumeroDocumento("12345678");
        pasajero1.setEmail("juan.perez@email.com");
        pasajero1.setTelefono("3001234567");
        pasajero1.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        pasajero1.setNacionalidad("Colombia");
        pasajeroRepository.save(pasajero1);

        Pasajero pasajero2 = new Pasajero();
        pasajero2.setPasajeroId("P002");
        pasajero2.setNombre("María");
        pasajero2.setApellido("García");
        pasajero2.setTipoDocumento("CC");
        pasajero2.setNumeroDocumento("87654321");
        pasajero2.setEmail("maria.garcia@email.com");
        pasajero2.setTelefono("3009876543");
        pasajero2.setFechaNacimiento(LocalDate.of(1985, 5, 15));
        pasajero2.setNacionalidad("Colombia");
        pasajeroRepository.save(pasajero2);

        Pasajero pasajero3 = new Pasajero();
        pasajero3.setPasajeroId("P003");
        pasajero3.setNombre("Carlos");
        pasajero3.setApellido("Rodríguez");
        pasajero3.setTipoDocumento("CC");
        pasajero3.setNumeroDocumento("11223344");
        pasajero3.setEmail("carlos.rodriguez@email.com");
        pasajero3.setTelefono("3012345678");
        pasajero3.setFechaNacimiento(LocalDate.of(1988, 8, 20));
        pasajero3.setNacionalidad("Colombia");
        pasajeroRepository.save(pasajero3);

        Pasajero pasajero4 = new Pasajero();
        pasajero4.setPasajeroId("P004");
        pasajero4.setNombre("Ana");
        pasajero4.setApellido("López");
        pasajero4.setTipoDocumento("CC");
        pasajero4.setNumeroDocumento("44332211");
        pasajero4.setEmail("ana.lopez@email.com");
        pasajero4.setTelefono("3023456789");
        pasajero4.setFechaNacimiento(LocalDate.of(1992, 12, 10));
        pasajero4.setNacionalidad("Colombia");
        pasajeroRepository.save(pasajero4);

        Pasajero pasajero5 = new Pasajero();
        pasajero5.setPasajeroId("P005");
        pasajero5.setNombre("Pedro");
        pasajero5.setApellido("Martínez");
        pasajero5.setTipoDocumento("CC");
        pasajero5.setNumeroDocumento("55667788");
        pasajero5.setEmail("pedro.martinez@email.com");
        pasajero5.setTelefono("3034567890");
        pasajero5.setFechaNacimiento(LocalDate.of(1987, 3, 25));
        pasajero5.setNacionalidad("Colombia");
        pasajeroRepository.save(pasajero5);

        // Crear reservas
        Reserva reserva1 = new Reserva();
        reserva1.setReservaVueloId("RSV001");
        reserva1.setReservaConfirmadaId("PNR001");
        reserva1.setVuelo(vuelo1);
        reserva1.setNumPasajeros(1);
        reserva1.setContactoReserva("Juan Pérez");
        reserva1.setDocumentoContacto("12345678");
        reserva1.setPrecioTotal(250000.0);
        reserva1.setEstado("CONFIRMADA");
        reserva1.setFechaCreacion(LocalDateTime.now());
        reserva1.setFechaConfirmacion(LocalDateTime.now());
        reserva1.setTransaccionId("TX001");
        reserva1.setObservaciones("Reserva confirmada");
        reserva1.setUrlComprobante("https://aerolinea.com/comprobantes/PNR001.pdf");
        reservaRepository.save(reserva1);

        Asiento asiento1 = asientoRepository.findById("AV001-01").orElse(null);
        if (asiento1 != null) {
            asiento1.setDisponible(false);
            asiento1.setReserva(reserva1);
            asientoRepository.save(asiento1);
        }

        Reserva reserva2 = new Reserva();
        reserva2.setReservaVueloId("RSV002");
        reserva2.setReservaConfirmadaId("PNR002");
        reserva2.setVuelo(vuelo2);
        reserva2.setNumPasajeros(1);
        reserva2.setContactoReserva("María García");
        reserva2.setDocumentoContacto("87654321");
        reserva2.setPrecioTotal(200000.0);
        reserva2.setEstado("CONFIRMADA");
        reserva2.setFechaCreacion(LocalDateTime.now());
        reserva2.setFechaConfirmacion(LocalDateTime.now());
        reserva2.setTransaccionId("TX002");
        reserva2.setObservaciones("Reserva confirmada");
        reserva2.setUrlComprobante("https://aerolinea.com/comprobantes/PNR002.pdf");
        reservaRepository.save(reserva2);

        Asiento asiento2 = asientoRepository.findById("AV002-02").orElse(null);
        if (asiento2 != null) {
            asiento2.setDisponible(false);
            asiento2.setReserva(reserva2);
            asientoRepository.save(asiento2);
        }

        Reserva reserva3 = new Reserva();
        reserva3.setReservaVueloId("RSV003");
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

        Asiento asiento3 = asientoRepository.findById("AV003-03").orElse(null);
        if (asiento3 != null) {
            asiento3.setReserva(reserva3);
            asientoRepository.save(asiento3);
        }
    }
}