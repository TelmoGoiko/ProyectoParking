package com.lksnext.parkingplantilla.model;

import java.io.Serializable;

/**
 * Clase que representa un vehículo del usuario
 */
public class Vehicle implements Serializable {

    private String id;
    private String name;
    private String licensePlate;
    private String brand;
    private String model;
    private VehicleType type;
    private boolean electric;
    private boolean forDisabled;

    // Enum para tipos de vehículo
    public enum VehicleType {
        CAR("Coche"),
        MOTORCYCLE("Moto");

        private final String displayName;

        VehicleType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructor vacío para Firebase
    public Vehicle() {
    }

    // Constructor completo
    public Vehicle(String id, String name, String licensePlate, String brand, String model, VehicleType type, boolean electric, boolean forDisabled) {
        this.id = id;
        this.name = name;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.electric = electric;
        this.forDisabled = forDisabled;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public boolean isElectric() {
        return electric;
    }

    public void setElectric(boolean electric) {
        this.electric = electric;
    }

    public boolean isForDisabled() {
        return forDisabled;
    }

    public void setForDisabled(boolean forDisabled) {
        this.forDisabled = forDisabled;
    }

    @Override
    public String toString() {
        return name + " (" + licensePlate + ")";
    }
}
