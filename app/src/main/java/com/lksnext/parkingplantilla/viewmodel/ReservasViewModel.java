package com.lksnext.parkingplantilla.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.data.IVehicleRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.domain.Reserva;
import com.lksnext.parkingplantilla.model.Vehicle;
import com.lksnext.parkingplantilla.util.IReservaNotificationScheduler;
import com.lksnext.parkingplantilla.util.NotificacionReservaScheduler;

import java.util.List;

public class ReservasViewModel extends androidx.lifecycle.AndroidViewModel {

    private final IVehicleRepository repository;
    private final IReservaNotificationScheduler notificationScheduler;
    private final MutableLiveData<List<Plaza>> availablePlazas = new MutableLiveData<>();
    private final MutableLiveData<List<Reserva>> userReservas = new MutableLiveData<>();
    private final MutableLiveData<Reserva> selectedReserva = new MutableLiveData<>();
    private final MutableLiveData<Boolean> reservaCreated = new MutableLiveData<>();
    private final MutableLiveData<Boolean> reservaUpdated = new MutableLiveData<>();
    private final MutableLiveData<Boolean> reservaDeleted = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ReservasViewModel(@NonNull Application application) {
        super(application);
        repository = com.lksnext.parkingplantilla.data.DataRepository.getInstance();
        notificationScheduler = new NotificacionReservaScheduler(application.getApplicationContext());
    }
    // Constructor para test
    protected ReservasViewModel(@NonNull Application application, IVehicleRepository repository) {
        super(application);
        this.repository = repository;
        this.notificationScheduler = null;
    }
    // Constructor para test con mock de notificaciones
    public ReservasViewModel(@NonNull Application application, IVehicleRepository repository, IReservaNotificationScheduler notificationScheduler) {
        super(application);
        this.repository = repository;
        this.notificationScheduler = notificationScheduler;
    }

    public LiveData<List<Plaza>> getAvailablePlazas() {
        return availablePlazas;
    }

    public LiveData<List<Reserva>> getUserReservas() {
        return userReservas;
    }

    public LiveData<Reserva> getSelectedReserva() {
        return selectedReserva;
    }

    public LiveData<Boolean> isReservaCreated() {
        return reservaCreated;
    }

    public LiveData<Boolean> isReservaUpdated() {
        return reservaUpdated;
    }

    public LiveData<Boolean> isReservaDeleted() {
        return reservaDeleted;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Cargar plazas disponibles
    public void loadAvailablePlazas(String fecha, Hora hora) {
        repository.getAvailablePlazas(fecha, hora, availablePlazas);
    }

    // Cargar reservas del usuario
    public void loadUserReservas() {
        String userId = repository.getCurrentUserId();
        if (userId != null) {
            repository.getReservasByUser(userId, userReservas);
        }
    }

    // Crear una nueva reserva
    public void createReserva(String fecha, Plaza plaza, Hora hora, String vehicleId) {
        String userId = repository.getCurrentUserId();
        if (userId != null) {
            Reserva reserva = new Reserva(fecha, vehicleId, "", plaza, hora);
            repository.createReserva(userId, reserva, new Callback() {
                @Override
                public void onSuccess() {
                    errorMessage.setValue(null); // Limpiar error tras éxito
                    reservaCreated.setValue(true);
                    loadUserReservas(); // Actualizar la lista de reservas
                    // Programar notificaciones locales solo si hay scheduler
                    if (notificationScheduler != null) {
                        notificationScheduler.programarNotificaciones(
                            fecha,
                            hora.getHoraInicio(),
                            String.valueOf(plaza.getId())
                        );
                    }
                }

                @Override
                public void onFailure() {
                    errorMessage.setValue("Error al crear la reserva");
                    reservaCreated.setValue(false);
                }

                @Override
                public void onFailure(String errorMessageStr) {
                    errorMessage.setValue(errorMessageStr);
                    reservaCreated.setValue(false);
                }
            });
        } else {
            errorMessage.setValue("No hay usuario autenticado");
            reservaCreated.setValue(false);
        }
    }

    // Obtener una reserva específica
    public void getReservaById(String reservaId) {
        String userId = repository.getCurrentUserId();
        if (userId != null) {
            repository.getReservaById(userId, reservaId, selectedReserva);
        }
    }

    // Actualizar una reserva existente
    public void updateReserva(Reserva reserva) {
        String userId = repository.getCurrentUserId();
        if (userId != null) {
            // Solo permitir edición si la reserva está en estado 'Confirmada'
            if (reserva.getEstado() != null && !"Confirmada".equalsIgnoreCase(reserva.getEstado())) {
                errorMessage.setValue("Solo se puede editar una reserva confirmada.");
                reservaUpdated.setValue(false);
                return;
            }
            repository.updateReserva(userId, reserva, new Callback() {
                @Override
                public void onSuccess() {
                    errorMessage.setValue(null); // Limpiar error tras éxito
                    reservaUpdated.setValue(true);
                    loadUserReservas(); // Actualizar la lista de reservas
                }

                @Override
                public void onFailure() {
                    errorMessage.setValue("Error al actualizar la reserva");
                    reservaUpdated.setValue(false);
                }

                @Override
                public void onFailure(String errorMessageStr) {
                    errorMessage.setValue(errorMessageStr);
                    reservaUpdated.setValue(false);
                }
            });
        } else {
            errorMessage.setValue("No hay usuario autenticado");
            reservaUpdated.setValue(false);
        }
    }

    // Eliminar una reserva
    public void eliminarReserva(String reservaId) {
        String userId = repository.getCurrentUserId();
        if (userId != null) {
            repository.deleteReserva(userId, reservaId, new Callback() {
                @Override
                public void onSuccess() {
                    errorMessage.setValue(null); // Limpiar error tras éxito
                    reservaDeleted.setValue(true);
                    loadUserReservas(); // Actualizar la lista de reservas
                }

                @Override
                public void onFailure() {
                    errorMessage.setValue("Error al eliminar la reserva");
                    reservaDeleted.setValue(false);
                }
            });
        } else {
            errorMessage.setValue("No hay usuario autenticado");
            reservaDeleted.setValue(false);
        }
    }

    // Verificar si hay un usuario autenticado
    public boolean isUserAuthenticated() {
        return repository.isUserAuthenticated();
    }

    public String getCurrentUserId() {
        return repository.getCurrentUserId();
    }
}
