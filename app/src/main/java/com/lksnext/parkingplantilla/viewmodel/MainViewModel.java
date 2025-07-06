package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.data.IVehicleRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.model.Vehicle;

import java.util.List;

public class MainViewModel extends ViewModel {
    final IVehicleRepository repository;
    final MutableLiveData<List<Vehicle>> vehiclesLiveData;

    // Constructor por defecto
    public MainViewModel() {
        this.repository = DataRepository.getInstance();
        this.vehiclesLiveData = new MutableLiveData<>();
    }

    // Constructor para test (permite inyectar mocks)
    protected MainViewModel(IVehicleRepository repository, MutableLiveData<List<Vehicle>> vehiclesLiveData) {
        this.repository = repository;
        this.vehiclesLiveData = vehiclesLiveData;
    }

    public LiveData<List<Vehicle>> getVehiclesLiveData() {
        return vehiclesLiveData;
    }

    public void loadVehicles(String userId) {
        repository.getVehicles(userId, vehiclesLiveData);
    }

    public void addVehicle(String userId, Vehicle vehicle, Callback callback) {
        repository.addVehicle(userId, vehicle, callback);
    }

    public void deleteVehicle(String userId, String vehicleId, Callback callback) {
        repository.deleteVehicle(userId, vehicleId, callback);
    }

    public void addOrUpdateVehicle(Vehicle vehicle, boolean isEdit, Callback callback) {
        repository.addOrUpdateVehicle(vehicle, isEdit, callback);
    }

    public String getCurrentUserId() {
        return repository.getCurrentUserId();
    }
}
