package com.lksnext.parkingplantilla.data;

import com.lksnext.parkingplantilla.domain.Callback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import androidx.annotation.Nullable;
import com.lksnext.parkingplantilla.model.Vehicle;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import androidx.lifecycle.MutableLiveData;

public class DataRepository {

    private static DataRepository instance;
    private final FirebaseAuth firebaseAuth;

    private DataRepository(){
        firebaseAuth = FirebaseAuth.getInstance();
    }

    //Creación de la instancia en caso de que no exista.
    public static synchronized DataRepository getInstance(){
        if (instance==null){
            instance = new DataRepository();
        }
        return instance;
    }

    //Petición del login.
    public void login(String email, String pass, Callback callback){
        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure();
                    }
                }
            });
    }

    // Guardar vehículo en Firestore
    public void addVehicle(String userId, Vehicle vehicle, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
            .collection("vehiculos").document(vehicle.getId())
            .set(vehicle)
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(e -> {
                android.util.Log.e("FIRESTORE_ERROR", "Error al guardar vehículo", e);
                callback.onFailure();
            });
    }

    // Leer vehículos del usuario en Firestore
    public void getVehicles(String userId, MutableLiveData<List<Vehicle>> liveData) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
            .collection("vehiculos")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Vehicle> vehicles = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Vehicle v = doc.toObject(Vehicle.class);
                    if (v != null) vehicles.add(v);
                }
                liveData.postValue(vehicles);
            })
            .addOnFailureListener(e -> {
                android.util.Log.e("FIRESTORE_ERROR", "Error al leer vehículos", e);
                liveData.postValue(new ArrayList<>());
            });
    }

    // Eliminar vehículo en Firestore
    public void deleteVehicle(String userId, String vehicleId, Callback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
            .collection("vehiculos").document(vehicleId)
            .delete()
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(e -> {
                android.util.Log.e("FIRESTORE_ERROR", "Error al eliminar vehículo", e);
                callback.onFailure();
            });
    }
}
