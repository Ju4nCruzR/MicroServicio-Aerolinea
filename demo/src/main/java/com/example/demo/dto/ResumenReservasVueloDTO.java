package com.example.demo.dto;

import java.util.UUID;

public class ResumenReservasVueloDTO {
    private UUID vueloId;
    private Integer totalReservas;
    private Integer reservasConfirmadas;
    private Integer reservasPendientes;
    private Integer reservasCanceladas;
    private Integer pasajerosConfirmados;
    private Double ingresosTotales;

    // Getters y setters
    public UUID getVueloId() {
        return vueloId;
    }

    public void setVueloId(UUID vueloId) {
        this.vueloId = vueloId;
    }

    public Integer getTotalReservas() {
        return totalReservas;
    }

    public void setTotalReservas(Integer totalReservas) {
        this.totalReservas = totalReservas;
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

    public Integer getReservasCanceladas() {
        return reservasCanceladas;
    }

    public void setReservasCanceladas(Integer reservasCanceladas) {
        this.reservasCanceladas = reservasCanceladas;
    }

    public Integer getPasajerosConfirmados() {
        return pasajerosConfirmados;
    }

    public void setPasajerosConfirmados(Integer pasajerosConfirmados) {
        this.pasajerosConfirmados = pasajerosConfirmados;
    }

    public Double getIngresosTotales() {
        return ingresosTotales;
    }

    public void setIngresosTotales(Double ingresosTotales) {
        this.ingresosTotales = ingresosTotales;
    }
}