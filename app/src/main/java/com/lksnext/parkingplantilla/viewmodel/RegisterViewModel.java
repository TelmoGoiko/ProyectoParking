package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.data.IVehicleRepository;
import com.lksnext.parkingplantilla.domain.Callback;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<Boolean> registered = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final IVehicleRepository dataRepository;

    public RegisterViewModel() {
        this(DataRepository.getInstance());
    }

    // Constructor para test
    protected RegisterViewModel(IVehicleRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public LiveData<Boolean> isRegistered() {
        return registered;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void registerUser(String username, String email, String password, String checkPassword) {
        // Log.d("RegisterViewModel", "Intentando registrar usuario: " + username + ", email: " + email);
        if (!password.equals(checkPassword)) {
            errorMessage.setValue("Las contraseñas no coinciden");
            registered.setValue(false);
            return;
        }
        dataRepository.checkUsernameExists(username, new Callback() {
            @Override
            public void onSuccess() {
                // El nombre de usuario no existe, proceder con el registro
                dataRepository.registerUser(username, email, password, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Log.d("RegisterViewModel", "Usuario registrado correctamente");
                        registered.setValue(true);
                    }

                    @Override
                    public void onFailure() {
                        // Log.e("RegisterViewModel", "Error al registrar el usuario");
                        errorMessage.setValue("Error al crear la cuenta. El correo electrónico puede estar ya en uso.");
                        registered.setValue(false);
                    }
                });
            }

            @Override
            public void onFailure() {
                // Log.e("RegisterViewModel", "El nombre de usuario ya está en uso");
                errorMessage.setValue("El nombre de usuario ya está en uso");
                registered.setValue(false);
            }
        });
    }
}
