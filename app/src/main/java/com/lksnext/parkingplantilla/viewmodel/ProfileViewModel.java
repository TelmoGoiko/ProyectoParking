package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> userName = new MutableLiveData<>();
    private final MutableLiveData<String> userEmail = new MutableLiveData<>();
    private final MutableLiveData<String> updateResult = new MutableLiveData<>();
    private final MutableLiveData<String> passwordResetResult = new MutableLiveData<>();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public LiveData<String> getUserName() { return userName; }
    public LiveData<String> getUserEmail() { return userEmail; }
    public LiveData<String> getUpdateResult() { return updateResult; }
    public LiveData<String> getPasswordResetResult() { return passwordResetResult; }

    public void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userName.setValue(user.getDisplayName());
            userEmail.setValue(user.getEmail());
        }
    }

    public void updateUserName(String newName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();
            user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userName.setValue(newName);
                        updateResult.setValue("Nombre actualizado correctamente");
                    } else {
                        updateResult.setValue("Error al actualizar el nombre");
                    }
                });
        }
    }

    public String getCurrentUserUid() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public void sendPasswordResetEmail(String email) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    passwordResetResult.setValue("Correo de recuperación enviado");
                } else {
                    passwordResetResult.setValue(task.getException() != null ? task.getException().getMessage() : "Error desconocido");
                }
            });
    }
}
