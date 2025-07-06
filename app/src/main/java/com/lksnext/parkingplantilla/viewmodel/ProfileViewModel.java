package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.data.IVehicleRepository;
import com.lksnext.parkingplantilla.domain.Callback;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final MutableLiveData<String> userEmail = new MutableLiveData<>();
    private final MutableLiveData<String> updateResult = new MutableLiveData<>();
    private final MutableLiveData<String> passwordResetResult = new MutableLiveData<>();
    private final IVehicleRepository dataRepository;

    public ProfileViewModel() {
        this(com.lksnext.parkingplantilla.data.DataRepository.getInstance());
    }
    // Constructor para test
    protected ProfileViewModel(IVehicleRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public LiveData<String> getUserName() { return userName; }
    public LiveData<String> getUserEmail() { return userEmail; }
    public LiveData<String> getUpdateResult() { return updateResult; }
    public LiveData<String> getPasswordResetResult() { return passwordResetResult; }

    public void loadUserData() {
        if (dataRepository.isUserAuthenticated()) {
            // Cargar email del usuario
            // userEmail.setValue(dataRepository.getCurrentUser().getEmail()); // No se usa en test
            String userId = dataRepository.getCurrentUserId();
            if (userId != null) {
                dataRepository.getUsernameFromFirestore(userId, userName);
            }
        }
    }

    public String getCurrentUserUid() {
        return dataRepository.getCurrentUserId();
    }

    public void sendPasswordResetEmail(String email) {
        dataRepository.sendPasswordResetEmail(email, new Callback() {
            @Override
            public void onSuccess() {
                passwordResetResult.setValue("Correo de recuperación enviado");
            }

            @Override
            public void onFailure() {
                passwordResetResult.setValue("Error al enviar el correo de recuperación");
            }
        });
    }

    public void signOut() {
        dataRepository.signOut();
    }
}
