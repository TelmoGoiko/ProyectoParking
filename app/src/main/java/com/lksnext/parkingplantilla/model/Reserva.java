package com.lksnext.parkingplantilla.model;

import java.io.Serializable;

public class Reserva implements Serializable {
    private String nombreParking;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String estado;
    private String vehicleId;
    private String nombreVehiculo;
    private String matriculaVehiculo;

    public Reserva(String nombreParking, String fecha, String horaInicio, String horaFin, String estado, String vehicleId, String nombreVehiculo, String matriculaVehiculo) {
        this.nombreParking = nombreParking;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.vehicleId = vehicleId;
        this.nombreVehiculo = nombreVehiculo;
        this.matriculaVehiculo = matriculaVehiculo;
    }

    public String getNombreParking() {
        return nombreParking;
    }

    public void setNombreParking(String nombreParking) {
        this.nombreParking = nombreParking;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getNombreVehiculo() {
        return nombreVehiculo;
    }

    public void setNombreVehiculo(String nombreVehiculo) {
        this.nombreVehiculo = nombreVehiculo;
    }

    public String getMatriculaVehiculo() {
        return matriculaVehiculo;
    }

    public void setMatriculaVehiculo(String matriculaVehiculo) {
        this.matriculaVehiculo = matriculaVehiculo;
    }
}
