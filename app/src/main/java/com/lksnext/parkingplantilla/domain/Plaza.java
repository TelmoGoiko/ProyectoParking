package com.lksnext.parkingplantilla.domain;

public class Plaza {

    long id;
    String tipo;

    public Plaza() {

    }

    public Plaza(long id, String tipo) {
        this.id = id;
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getId() {
        return (int) id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
