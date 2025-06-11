package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.domain.Callback;

public class RegisterViewModel extends ViewModel {
    // Aquí puedes declarar los LiveData y métodos necesarios para la vista de registro
    // Por ejemplo, un LiveData para el email, contraseña y usuario

    private final MutableLiveData<Boolean> registered = new MutableLiveData<>();

    public LiveData<Boolean> isRegistered() {
        return registered;
    }

    public void registerUser(String email, String password) {
        // Simulación de registro (aquí iría la lógica real, por ejemplo, llamada a un repositorio o API)
        // Si el registro es exitoso:
        registered.setValue(true);
        // Si falla, puedes poner: registered.setValue(false);
    }
}
