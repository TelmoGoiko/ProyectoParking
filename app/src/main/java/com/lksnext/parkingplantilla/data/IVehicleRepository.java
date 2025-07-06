package com.lksnext.parkingplantilla.data;

import androidx.lifecycle.MutableLiveData;

import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.model.Vehicle;

import java.util.List;

public interface IVehicleRepository {
    void getVehicles(String userId, MutableLiveData<List<Vehicle>> vehiclesLiveData);
    void addVehicle(String userId, Vehicle vehicle, Callback callback);
    void deleteVehicle(String userId, String vehicleId, Callback callback);
    void addOrUpdateVehicle(Vehicle vehicle, boolean isEdit, Callback callback);
    boolean isUserAuthenticated();
    String getCurrentUserId();
    void getUsernameFromFirestore(String userId, androidx.lifecycle.MutableLiveData<String> usernameLiveData);
    void sendPasswordResetEmail(String email, com.lksnext.parkingplantilla.domain.Callback callback);
    void signOut();
    void checkUsernameExists(String username, Callback callback);
    void registerUser(String username, String email, String password, Callback callback);
    void login(String email, String pass, Callback callback);
    void findUserByUsername(String username, Callback callback, MutableLiveData<String> emailResult);
    void getAvailablePlazas(String fecha, com.lksnext.parkingplantilla.domain.Hora hora, androidx.lifecycle.MutableLiveData<java.util.List<com.lksnext.parkingplantilla.domain.Plaza>> liveData);
    void getReservasByUser(String userId, androidx.lifecycle.MutableLiveData<java.util.List<com.lksnext.parkingplantilla.domain.Reserva>> liveData);
    void createReserva(String userId, com.lksnext.parkingplantilla.domain.Reserva reserva, com.lksnext.parkingplantilla.domain.Callback callback);
    void updateReserva(String userId, com.lksnext.parkingplantilla.domain.Reserva reserva, com.lksnext.parkingplantilla.domain.Callback callback);
    void deleteReserva(String userId, String reservaId, com.lksnext.parkingplantilla.domain.Callback callback);
    void getReservaById(String userId, String reservaId, androidx.lifecycle.MutableLiveData<com.lksnext.parkingplantilla.domain.Reserva> liveData);
}