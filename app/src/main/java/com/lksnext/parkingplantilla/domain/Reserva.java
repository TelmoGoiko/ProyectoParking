package com.lksnext.parkingplantilla.domain;

public class Reserva {

    String fecha, vehicleId, id, estado;

    Plaza plaza;

    Hora hora;

    public Reserva() {

    }

    public Reserva(String fecha, String vehicleId, String id, Plaza plaza, Hora hora) {
        this.fecha = fecha;
        this.vehicleId = vehicleId;
        this.plaza = plaza;
        this.hora = hora;
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Plaza getPlazaId() {
        return plaza;
    }

    public void setPlazaId(Plaza plaza) {
        this.plaza = plaza;
    }

    public Hora getHoraInicio() {
        return hora;
    }

    public void setHoraInicio(Hora hora) {
        this.hora = hora;
    }

    public Hora getHoraFin() {
        return hora;
    }

    public void setHoraFin(Hora hora) {
        this.hora = hora;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
