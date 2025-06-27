package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

public class RecoverPasswordViewModel extends ViewModel {
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    success.setValue(true);
                } else {
                    error.setValue(task.getException() != null ? task.getException().getMessage() : "Error desconocido");
                }
            });
    }
}

