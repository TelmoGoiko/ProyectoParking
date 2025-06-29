package com.lksnext.parkingplantilla.data;

import com.lksnext.parkingplantilla.domain.Callback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    // Guardar vehículo en Firebase
    public void addVehicle(String userId, Vehicle vehicle, Callback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("usuarios").child(userId).child("vehiculos").child(vehicle.getId());
        ref.setValue(vehicle)
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(e -> callback.onFailure());
    }

    // Leer vehículos del usuario en Firebase
    public void getVehicles(String userId, MutableLiveData<List<Vehicle>> liveData) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("usuarios").child(userId).child("vehiculos");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Vehicle> vehicles = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Vehicle v = child.getValue(Vehicle.class);
                    if (v != null) vehicles.add(v);
                }
                liveData.postValue(vehicles);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                liveData.postValue(new ArrayList<>());
            }
        });
    }

    // Eliminar vehículo en Firebase
    public void deleteVehicle(String userId, String vehicleId, Callback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("usuarios").child(userId).child("vehiculos").child(vehicleId);
        ref.removeValue()
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(e -> callback.onFailure());
    }
}
