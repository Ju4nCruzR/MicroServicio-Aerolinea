package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import com.example.demo.service.ReporteService;

@RestController
@RequestMapping("/v1/admin/reportes")
@Validated
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    /**
     * GET /v1/admin/reportes/ocupacion
     * Genera reporte de ocupación de vuelos
     */
    @GetMapping("/ocupacion")
    public ResponseEntity<List<ReporteService.ReporteOcupacion>> obtenerReporteOcupacion() {
        try {
            List<ReporteService.ReporteOcupacion> reporte = reporteService.generarReporteOcupacion();
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * GET /v1/admin/reportes/rutas-populares
     * Obtiene las 3 rutas más populares basándose en reservas confirmadas
     */
    @GetMapping("/rutas-populares")
    public ResponseEntity<List<ReporteService.RutaPopular>> obtenerRutasPopulares() {
        try {
            List<ReporteService.RutaPopular> rutasPopulares = reporteService.calcularRutasPopulares();
            return ResponseEntity.ok(rutasPopulares);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}