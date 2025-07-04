package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;

public class RecoverPasswordViewModel extends ViewModel {
    private final MutableLiveData<Boolean> success = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final DataRepository dataRepository;

    public RecoverPasswordViewModel() {
        dataRepository = DataRepository.getInstance();
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void sendPasswordResetEmail(String email) {
        dataRepository.sendPasswordResetEmail(email, new Callback() {
            @Override
            public void onSuccess() {
                success.setValue(true);
            }

            @Override
            public void onFailure() {
                error.setValue("Error al enviar el correo de recuperación");
            }
        });
    }
}
