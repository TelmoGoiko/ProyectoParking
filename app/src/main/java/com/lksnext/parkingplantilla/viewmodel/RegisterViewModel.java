package com.lksnext.parkingplantilla.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class RegisterViewModel extends ViewModel {
    // Aquí puedes declarar los LiveData y métodos necesarios para la vista de registro
    // Por ejemplo, un LiveData para el email, contraseña y usuario

    private final MutableLiveData<Boolean> registered = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public LiveData<Boolean> isRegistered() {
        return registered;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void registerUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    registered.setValue(true);
                } else {
                    registered.setValue(false);
                    Exception e = task.getException();
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        errorMessage.setValue("El usuario ya existe");
                    } else if (e != null) {
                        errorMessage.setValue(e.getLocalizedMessage());
                    } else {
                        errorMessage.setValue("Error desconocido al registrar");
                    }
                }
            });
    }
}
