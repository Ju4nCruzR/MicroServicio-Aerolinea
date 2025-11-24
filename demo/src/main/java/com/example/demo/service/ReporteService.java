package com.example.demo.service;

import com.example.demo.entity.Vuelo;
import com.example.demo.entity.Reserva;
import com.example.demo.repository.VueloRepository;
import com.example.demo.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;

@Service
public class ReporteService {

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    /**
     * Calcula las 3 rutas más populares basándose en las reservas confirmadas
     */
    public List<RutaPopular> calcularRutasPopulares() {
        // Obtener todas las reservas confirmadas
        List<Reserva> reservasConfirmadas = reservaRepository.findAll().stream()
            .filter(reserva -> "CONFIRMADA".equals(reserva.getEstado()))
            .collect(Collectors.toList());

        // Agrupar por ruta (origen-destino) y contar
        Map<String, Long> rutasCount = reservasConfirmadas.stream()
            .filter(reserva -> reserva.getVuelo() != null && 
                              reserva.getVuelo().getOrigen() != null && 
                              reserva.getVuelo().getDestino() != null)
            .collect(Collectors.groupingBy(
                reserva -> reserva.getVuelo().getOrigen().getCodigoIATA() + "-" + 
                          reserva.getVuelo().getDestino().getCodigoIATA(),
                Collectors.counting()
            ));

        // Convertir a lista de RutaPopular y ordenar por popularidad
        return rutasCount.entrySet().stream()
            .map(entry -> {
                String[] ciudades = entry.getKey().split("-");
                RutaPopular ruta = new RutaPopular();
                ruta.setOrigen(ciudades[0]);
                ruta.setDestino(ciudades[1]);
                ruta.setCantidadReservas(entry.getValue().intValue());
                
                // Calcular pasajeros totales para esta ruta
                int totalPasajeros = reservasConfirmadas.stream()
                    .filter(r -> r.getVuelo() != null &&
                                r.getVuelo().getOrigen() != null &&
                                r.getVuelo().getDestino() != null &&
                                entry.getKey().equals(r.getVuelo().getOrigen().getCodigoIATA() + "-" + 
                                                     r.getVuelo().getDestino().getCodigoIATA()))
                    .mapToInt(Reserva::getNumPasajeros)
                    .sum();
                ruta.setTotalPasajeros(totalPasajeros);
                
                return ruta;
            })
            .sorted(Comparator.comparing(RutaPopular::getCantidadReservas).reversed())
            .limit(3) // Solo las 3 más populares
            .collect(Collectors.toList());
    }

    /**
     * Genera reporte de ocupación de vuelos
     */
    public List<ReporteOcupacion> generarReporteOcupacion() {
        // Obtener todos los vuelos programados o completados
        List<Vuelo> vuelosReporte = vueloRepository.findAll().stream()
            .filter(vuelo -> "PROGRAMADO".equals(vuelo.getEstado()) || 
                            "COMPLETADO".equals(vuelo.getEstado()) ||
                            "EN_VUELO".equals(vuelo.getEstado()))
            .collect(Collectors.toList());

        return vuelosReporte.stream()
            .map(vuelo -> {
                ReporteOcupacion reporte = new ReporteOcupacion();
                reporte.setVueloId(vuelo.getVueloId());
                reporte.setAerolinea(vuelo.getAerolinea());
                reporte.setOrigen(vuelo.getOrigen() != null ? vuelo.getOrigen().getCodigoIATA() : "N/A");
                reporte.setDestino(vuelo.getDestino() != null ? vuelo.getDestino().getCodigoIATA() : "N/A");
                reporte.setFechaSalida(vuelo.getFechaSalida());
                reporte.setCapacidadTotal(vuelo.getCapacidadTotal());
                reporte.setDisponibilidad(vuelo.getDisponibilidad());
                
                // Calcular asientos ocupados
                int asientosOcupados = vuelo.getCapacidadTotal() - vuelo.getDisponibilidad();
                reporte.setAsientosOcupados(asientosOcupados);
                
                // Calcular porcentaje de ocupación
                double porcentajeOcupacion = vuelo.getCapacidadTotal() > 0 ? 
                    (double) asientosOcupados / vuelo.getCapacidadTotal() * 100 : 0.0;
                reporte.setPorcentajeOcupacion(Math.round(porcentajeOcupacion * 100.0) / 100.0); // Redondear a 2 decimales
                
                // Contar reservas por estado usando repository para evitar lazy loading
                List<Reserva> reservasVuelo = reservaRepository.findAll().stream()
                    .filter(r -> r.getVuelo() != null && vuelo.getVueloId().equals(r.getVuelo().getVueloId()))
                    .collect(Collectors.toList());
                
                reporte.setReservasConfirmadas((int) reservasVuelo.stream()
                    .filter(r -> "CONFIRMADA".equals(r.getEstado()))
                    .count());
                reporte.setReservasPendientes((int) reservasVuelo.stream()
                    .filter(r -> "PENDIENTE".equals(r.getEstado()))
                    .count());
                
                reporte.setEstadoVuelo(vuelo.getEstado());
                
                return reporte;
            })
            .sorted(Comparator.comparing(ReporteOcupacion::getPorcentajeOcupacion).reversed())
            .collect(Collectors.toList());
    }

    // DTOs para los reportes
    public static class RutaPopular {
        private String origen;
        private String destino;
        private Integer cantidadReservas;
        private Integer totalPasajeros;

        // Getters y setters
        public String getOrigen() {
            return origen;
        }

        public void setOrigen(String origen) {
            this.origen = origen;
        }

        public String getDestino() {
            return destino;
        }

        public void setDestino(String destino) {
            this.destino = destino;
        }

        public Integer getCantidadReservas() {
            return cantidadReservas;
        }

        public void setCantidadReservas(Integer cantidadReservas) {
            this.cantidadReservas = cantidadReservas;
        }

        public Integer getTotalPasajeros() {
            return totalPasajeros;
        }

        public void setTotalPasajeros(Integer totalPasajeros) {
            this.totalPasajeros = totalPasajeros;
        }
    }

    public static class ReporteOcupacion {
        private java.util.UUID vueloId;
        private String aerolinea;
        private String origen;
        private String destino;
        private java.time.LocalDateTime fechaSalida;
        private Integer capacidadTotal;
        private Integer disponibilidad;
        private Integer asientosOcupados;
        private Double porcentajeOcupacion;
        private Integer reservasConfirmadas;
        private Integer reservasPendientes;
        private String estadoVuelo;

        // Getters y setters
        public java.util.UUID getVueloId() {
            return vueloId;
        }

        public void setVueloId(java.util.UUID vueloId) {
            this.vueloId = vueloId;
        }

        public String getAerolinea() {
            return aerolinea;
        }

        public void setAerolinea(String aerolinea) {
            this.aerolinea = aerolinea;
        }

        public String getOrigen() {
            return origen;
        }

        public void setOrigen(String origen) {
            this.origen = origen;
        }

        public String getDestino() {
            return destino;
        }

        public void setDestino(String destino) {
            this.destino = destino;
        }

        public java.time.LocalDateTime getFechaSalida() {
            return fechaSalida;
        }

        public void setFechaSalida(java.time.LocalDateTime fechaSalida) {
            this.fechaSalida = fechaSalida;
        }

        public Integer getCapacidadTotal() {
            return capacidadTotal;
        }

        public void setCapacidadTotal(Integer capacidadTotal) {
            this.capacidadTotal = capacidadTotal;
        }

        public Integer getDisponibilidad() {
            return disponibilidad;
        }

        public void setDisponibilidad(Integer disponibilidad) {
            this.disponibilidad = disponibilidad;
        }

        public Integer getAsientosOcupados() {
            return asientosOcupados;
        }

        public void setAsientosOcupados(Integer asientosOcupados) {
            this.asientosOcupados = asientosOcupados;
        }

        public Double getPorcentajeOcupacion() {
            return porcentajeOcupacion;
        }

        public void setPorcentajeOcupacion(Double porcentajeOcupacion) {
            this.porcentajeOcupacion = porcentajeOcupacion;
        }

        public Integer getReservasConfirmadas() {
            return reservasConfirmadas;
        }

        public void setReservasConfirmadas(Integer reservasConfirmadas) {
            this.reservasConfirmadas = reservasConfirmadas;
        }

        public Integer getReservasPendientes() {
            return reservasPendientes;
        }

        public void setReservasPendientes(Integer reservasPendientes) {
            this.reservasPendientes = reservasPendientes;
        }

        public String getEstadoVuelo() {
            return estadoVuelo;
        }

        public void setEstadoVuelo(String estadoVuelo) {
            this.estadoVuelo = estadoVuelo;
        }
    }
}