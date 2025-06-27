package com.lksnext.parkingplantilla.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterViewModel extends ViewModel {
    // Aquí puedes declarar los LiveData y métodos necesarios para la vista de registro
    // Por ejemplo, un LiveData para el email, contraseña y usuario

    private final MutableLiveData<Boolean> registered = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public LiveData<Boolean> isRegistered() {
        return registered;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void registerUser(String username, String email, String password, String checkPassword) {
        Log.d("RegisterViewModel", "Intentando registrar usuario: " + username + ", email: " + email);
        if (!password.equals(checkPassword)) {
            errorMessage.setValue("Las contraseñas no coinciden");
            registered.setValue(false);
            return;
        }
        // Comprobar si el nombre de usuario ya existe en Firestore
        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                Log.d("RegisterViewModel", "Consulta Firestore exitosa. Documentos encontrados: " + queryDocumentSnapshots.size());
                if (!queryDocumentSnapshots.isEmpty()) {
                    errorMessage.setValue("El nombre de usuario ya está en uso");
                    registered.setValue(false);
                } else {
                    // Crear usuario en Firebase Auth
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String uid = firebaseAuth.getCurrentUser().getUid();
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("username", username);
                                userMap.put("email", email);
                                userMap.put("uid", uid);
                                firestore.collection("users").document(uid).set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("RegisterViewModel", "Usuario guardado en Firestore correctamente");
                                        registered.setValue(true);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("RegisterViewModel", "Error al guardar usuario en Firestore: " + e.getMessage(), e);
                                        errorMessage.setValue("Error al guardar usuario en Firestore: " + e.getMessage());
                                        registered.setValue(false);
                                    });
                            } else {
                                registered.setValue(false);
                                Exception e = task.getException();
                                Log.e("RegisterViewModel", "Error al crear usuario en Auth: " + (e != null ? e.getMessage() : "desconocido"), e);
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
            })
            .addOnFailureListener(e -> {
                Log.e("RegisterViewModel", "Error al comprobar nombre de usuario: " + e.getMessage(), e);
                errorMessage.setValue("Error al comprobar nombre de usuario: " + e.getMessage());
                registered.setValue(false);
            });
    }
}
