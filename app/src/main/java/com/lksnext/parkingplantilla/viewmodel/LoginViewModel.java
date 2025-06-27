package com.lksnext.parkingplantilla.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;

public class LoginViewModel extends ViewModel {

    // Aquí puedes declarar los LiveData y métodos necesarios para la vista de inicio de sesión
    MutableLiveData<Boolean> logged = new MutableLiveData<>(null);

    public LiveData<Boolean> isLogged(){
        return logged;
    }

    public void loginUser(String userOrEmail, String password) {
        if (userOrEmail.contains("@")) {
            // Es un email
            DataRepository.getInstance().login(userOrEmail, password, new Callback() {
                @Override
                public void onSuccess() {
                    logged.setValue(Boolean.TRUE);
                }
                @Override
                public void onFailure() {
                    logged.setValue(Boolean.FALSE);
                }
            });
        } else {
            // Es un nombre de usuario, buscar el email en Firestore
            FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("username", userOrEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty() && queryDocumentSnapshots.getDocuments().size() > 0) {
                        String email = queryDocumentSnapshots.getDocuments().get(0).getString("email");
                        if (email != null) {
                            DataRepository.getInstance().login(email, password, new Callback() {
                                @Override
                                public void onSuccess() {
                                    logged.setValue(Boolean.TRUE);
                                }
                                @Override
                                public void onFailure() {
                                    logged.setValue(Boolean.FALSE);
                                }
                            });
                        } else {
                            logged.setValue(Boolean.FALSE);
                        }
                    } else {
                        logged.setValue(Boolean.FALSE);
                    }
                })
                .addOnFailureListener(e -> logged.setValue(Boolean.FALSE));
        }
    }
}
