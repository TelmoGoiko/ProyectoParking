package com.lksnext.parkingplantilla.data;

import com.lksnext.parkingplantilla.domain.Callback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseUser;
import com.lksnext.parkingplantilla.model.Vehicle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.lifecycle.MutableLiveData;

public class DataRepository {

    private static DataRepository instance;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;

    private final String users = "users";
    private final String vehicles = "vehiculos";

    private final String FIRESTORE_ERROR = "FIRESTORE_ERROR";
    
    // Reservas
    private final String reservas = "reservas";


    private DataRepository(){
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    // Creación de la instancia en caso de que no exista.
    public static synchronized DataRepository getInstance(){
        if (instance==null){
            instance = new DataRepository();
        }
        return instance;
    }

    // Petición del login.
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

    // Google Sign-In Authentication
    public void firebaseAuthWithGoogle(GoogleSignInAccount account, Callback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        saveUserToFirestore(user);
                    }
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            });
    }

    // Save user to Firestore if new
    private void saveUserToFirestore(FirebaseUser user) {
        firestore.collection(users).document(user.getUid()).get()
            .addOnSuccessListener(doc -> {
                if (!doc.exists()) {
                    // New user, save data
                    String username = user.getDisplayName() != null ?
                        user.getDisplayName() : user.getEmail().split("@")[0];

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("username", username);
                    userData.put("email", user.getEmail());
                    userData.put("uid", user.getUid());

                    firestore.collection(users).document(user.getUid()).set(userData);
                }
            });
    }

    // Password reset
    public void sendPasswordResetEmail(String email, Callback callback) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            });
    }

    // Guardar vehículo en Firestore
    public void addVehicle(String userId, Vehicle vehicle, Callback callback) {
        firestore.collection(users).document(userId)
            .collection(vehicles).document(vehicle.getId())
            .set(vehicle)
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(e -> {
                android.util.Log.e(FIRESTORE_ERROR, "Error al guardar vehículo", e);
                callback.onFailure();
            });
    }

    // Leer vehículos del usuario en Firestore
    public void getVehicles(String userId, MutableLiveData<List<Vehicle>> liveData) {
        firestore.collection(users).document(userId)
            .collection(vehicles)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Vehicle> vehicles = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Vehicle v = doc.toObject(Vehicle.class);
                    vehicles.add(v);
                }
                liveData.postValue(vehicles);
            })
            .addOnFailureListener(e -> {
                android.util.Log.e(FIRESTORE_ERROR, "Error al leer vehículos", e);
                liveData.postValue(new ArrayList<>());
            });
    }

    // Eliminar vehículo en Firestore
    public void deleteVehicle(String userId, String vehicleId, Callback callback) {
        firestore.collection(users).document(userId)
            .collection(vehicles).document(vehicleId)
            .delete()
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(e -> {
                android.util.Log.e(FIRESTORE_ERROR, "Error al eliminar vehículo", e);
                callback.onFailure();
            });
    }

    // Find user by username
    public void findUserByUsername(String username, Callback callback, MutableLiveData<String> emailResult) {
        firestore.collection(users)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty() && !queryDocumentSnapshots.getDocuments().isEmpty()) {
                    String email = queryDocumentSnapshots.getDocuments().get(0).getString("email");
                    if (email != null) {
                        emailResult.setValue(email);
                        callback.onSuccess();
                    } else {
                        callback.onFailure();
                    }
                } else {
                    callback.onFailure();
                }
            })
            .addOnFailureListener(e -> callback.onFailure());
    }

    // Check if username exists
    public void checkUsernameExists(String username, Callback callback) {
        firestore.collection(users)
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    callback.onFailure(); // Username exists, so registration should fail
                } else {
                    callback.onSuccess(); // Username doesn't exist, can proceed
                }
            })
            .addOnFailureListener(e -> callback.onFailure());
    }

    // Register user with email and password
    public void registerUser(String username, String email, String password, Callback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uid = firebaseAuth.getCurrentUser().getUid();
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("username", username);
                    userMap.put("email", email);
                    userMap.put("uid", uid);
                    firestore.collection(users).document(uid).set(userMap)
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(e -> callback.onFailure());
                } else {
                    callback.onFailure();
                }
            });
    }

    // Crear una nueva reserva con validaciones de solapamiento, duración y rango de fechas
    public void createReserva(String userId, com.lksnext.parkingplantilla.domain.Reserva reserva, Callback callback) {
        // Validación: duración máxima 8 horas
        if (reserva.getHoraInicio() != null) {
            long duracionSegundos = reserva.getHoraInicio().getHoraFin() - reserva.getHoraInicio().getHoraInicio();
            if (duracionSegundos > 8 * 3600) {
                callback.onFailure();
                return;
            }
        }

        // Validación: fecha entre hoy y 7 días naturales
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        java.util.Calendar hoy = java.util.Calendar.getInstance();
        java.util.Calendar fechaReserva = java.util.Calendar.getInstance();
        try {
            fechaReserva.setTime(sdf.parse(reserva.getFecha()));
        } catch (Exception e) {
            callback.onFailure();
            return;
        }
        java.util.Calendar maxFecha = (java.util.Calendar) hoy.clone();
        maxFecha.add(java.util.Calendar.DAY_OF_YEAR, 7);
        // Limpiar horas para comparar solo fechas
        hoy.set(java.util.Calendar.HOUR_OF_DAY, 0);
        hoy.set(java.util.Calendar.MINUTE, 0);
        hoy.set(java.util.Calendar.SECOND, 0);
        hoy.set(java.util.Calendar.MILLISECOND, 0);
        fechaReserva.set(java.util.Calendar.HOUR_OF_DAY, 0);
        fechaReserva.set(java.util.Calendar.MINUTE, 0);
        fechaReserva.set(java.util.Calendar.SECOND, 0);
        fechaReserva.set(java.util.Calendar.MILLISECOND, 0);
        maxFecha.set(java.util.Calendar.HOUR_OF_DAY, 0);
        maxFecha.set(java.util.Calendar.MINUTE, 0);
        maxFecha.set(java.util.Calendar.SECOND, 0);
        maxFecha.set(java.util.Calendar.MILLISECOND, 0);
        if (fechaReserva.before(hoy) || fechaReserva.after(maxFecha)) {
            callback.onFailure();
            return;
        }

        // Validación: no solapamiento de reservas para la misma plaza
        firestore.collectionGroup(reservas)
            .whereEqualTo("fecha", reserva.getFecha())
            .whereEqualTo("plazaId.id", reserva.getPlazaId().getId())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                boolean solapada = false;
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    com.lksnext.parkingplantilla.domain.Reserva r = doc.toObject(com.lksnext.parkingplantilla.domain.Reserva.class);
                    if (r.getId().equals(reserva.getId())) continue; // Permitir editar la misma reserva
                    if (r.getHoraInicio() != null && reserva.getHoraInicio() != null) {
                        long start1 = r.getHoraInicio().getHoraInicio();
                        long end1 = r.getHoraInicio().getHoraFin();
                        long start2 = reserva.getHoraInicio().getHoraInicio();
                        long end2 = reserva.getHoraInicio().getHoraFin();
                        // Si se solapan
                        if (start1 < end2 && start2 < end1) {
                            solapada = true;
                            break;
                        }
                    }
                }
                if (solapada) {
                    callback.onFailure();
                } else {
                    // Si pasa todas las validaciones, guardar la reserva
                    if (reserva.getId() == null || reserva.getId().isEmpty()) {
                        reserva.setId(firestore.collection(reservas).document().getId());
                    }
                    firestore.collection(users).document(userId)
                        .collection(reservas).document(reserva.getId())
                        .set(reserva)
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(e -> {
                            android.util.Log.e(FIRESTORE_ERROR, "Error al guardar reserva", e);
                            callback.onFailure();
                        });
                }
            })
            .addOnFailureListener(e -> {
                android.util.Log.e(FIRESTORE_ERROR, "Error al comprobar solapamiento de reservas", e);
                callback.onFailure();
            });
    }

    // Obtener todas las reservas de un usuario
    public void getReservasByUser(String userId, MutableLiveData<List<com.lksnext.parkingplantilla.domain.Reserva>> liveData) {
        firestore.collection(users).document(userId)
            .collection(reservas)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<com.lksnext.parkingplantilla.domain.Reserva> reservasList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    com.lksnext.parkingplantilla.domain.Reserva reserva = doc.toObject(com.lksnext.parkingplantilla.domain.Reserva.class);
                    if (reserva != null) reservasList.add(reserva);
                }
                liveData.postValue(reservasList);
            })
            .addOnFailureListener(e -> {
                android.util.Log.e(FIRESTORE_ERROR, "Error al obtener reservas", e);
                liveData.postValue(new ArrayList<>());
            });
    }

    // Obtener una reserva específica
    public void getReservaById(String userId, String reservaId, MutableLiveData<com.lksnext.parkingplantilla.domain.Reserva> liveData) {
        firestore.collection(users).document(userId)
            .collection(reservas).document(reservaId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                com.lksnext.parkingplantilla.domain.Reserva reserva = documentSnapshot.toObject(com.lksnext.parkingplantilla.domain.Reserva.class);
                liveData.postValue(reserva);
            })
            .addOnFailureListener(e -> {
                android.util.Log.e(FIRESTORE_ERROR, "Error al obtener reserva", e);
                liveData.postValue(null);
            });
    }

    // Actualizar una reserva existente
    public void updateReserva(String userId, com.lksnext.parkingplantilla.domain.Reserva reserva, Callback callback) {
        // Validación: duración máxima 8 horas
        if (reserva.getHoraInicio() != null) {
            long duracionSegundos = reserva.getHoraInicio().getHoraFin() - reserva.getHoraInicio().getHoraInicio();
            if (duracionSegundos > 8 * 3600) {
                callback.onFailure();
                return;
            }
        }
        // Validación: fecha entre hoy y 7 días naturales
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        java.util.Calendar hoy = java.util.Calendar.getInstance();
        java.util.Calendar fechaReserva = java.util.Calendar.getInstance();
        try {
            fechaReserva.setTime(sdf.parse(reserva.getFecha()));
        } catch (Exception e) {
            callback.onFailure();
            return;
        }
        java.util.Calendar maxFecha = (java.util.Calendar) hoy.clone();
        maxFecha.add(java.util.Calendar.DAY_OF_YEAR, 7);
        hoy.set(java.util.Calendar.HOUR_OF_DAY, 0);
        hoy.set(java.util.Calendar.MINUTE, 0);
        hoy.set(java.util.Calendar.SECOND, 0);
        hoy.set(java.util.Calendar.MILLISECOND, 0);
        fechaReserva.set(java.util.Calendar.HOUR_OF_DAY, 0);
        fechaReserva.set(java.util.Calendar.MINUTE, 0);
        fechaReserva.set(java.util.Calendar.SECOND, 0);
        fechaReserva.set(java.util.Calendar.MILLISECOND, 0);
        maxFecha.set(java.util.Calendar.HOUR_OF_DAY, 0);
        maxFecha.set(java.util.Calendar.MINUTE, 0);
        maxFecha.set(java.util.Calendar.SECOND, 0);
        maxFecha.set(java.util.Calendar.MILLISECOND, 0);
        if (fechaReserva.before(hoy) || fechaReserva.after(maxFecha)) {
            callback.onFailure();
            return;
        }
        // Validación: no solapamiento de reservas para la misma plaza
        firestore.collectionGroup(reservas)
            .whereEqualTo("fecha", reserva.getFecha())
            .whereEqualTo("plazaId.id", reserva.getPlazaId().getId())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                boolean solapada = false;
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    com.lksnext.parkingplantilla.domain.Reserva r = doc.toObject(com.lksnext.parkingplantilla.domain.Reserva.class);
                    if (r.getId().equals(reserva.getId())) continue; // Permitir editar la misma reserva
                    if (r.getHoraInicio() != null && reserva.getHoraInicio() != null) {
                        long start1 = r.getHoraInicio().getHoraInicio();
                        long end1 = r.getHoraInicio().getHoraFin();
                        long start2 = reserva.getHoraInicio().getHoraInicio();
                        long end2 = reserva.getHoraInicio().getHoraFin();
                        if (start1 < end2 && start2 < end1) {
                            solapada = true;
                            break;
                        }
                    }
                }
                if (solapada) {
                    callback.onFailure();
                } else {
                    firestore.collection(users).document(userId)
                        .collection(reservas).document(reserva.getId())
                        .set(reserva)
                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                        .addOnFailureListener(e -> {
                            android.util.Log.e(FIRESTORE_ERROR, "Error al actualizar reserva", e);
                            callback.onFailure();
                        });
                }
            })
            .addOnFailureListener(e -> {
                android.util.Log.e(FIRESTORE_ERROR, "Error al comprobar solapamiento de reservas", e);
                callback.onFailure();
            });
    }

    // Eliminar una reserva
    public void deleteReserva(String userId, String reservaId, Callback callback) {
        firestore.collection(users).document(userId)
            .collection(reservas).document(reservaId)
            .delete()
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(e -> {
                android.util.Log.e(FIRESTORE_ERROR, "Error al eliminar reserva", e);
                callback.onFailure();
            });
    }

    // Obtener plazas disponibles
    public void getAvailablePlazas(String fecha, com.lksnext.parkingplantilla.domain.Hora hora, MutableLiveData<List<com.lksnext.parkingplantilla.domain.Plaza>> liveData) {
        // Simular plazas disponibles (esto debería consultarse en Firebase)
        List<com.lksnext.parkingplantilla.domain.Plaza> plazas = new ArrayList<>();
        // Plazas para coches normales
        for (int i = 1; i <= 10; i++) {
            plazas.add(new com.lksnext.parkingplantilla.domain.Plaza(i, "normal"));
        }
        // Plazas para coches minusválidos
        for (int i = 11; i <= 13; i++) {
            plazas.add(new com.lksnext.parkingplantilla.domain.Plaza(i, "minusvalido"));
        }
        // Plazas para coches eléctricos
        for (int i = 14; i <= 16; i++) {
            plazas.add(new com.lksnext.parkingplantilla.domain.Plaza(i, "electrico"));
        }
        // Plazas para coches eléctricos minusválidos
        for (int i = 17; i <= 18; i++) {
            plazas.add(new com.lksnext.parkingplantilla.domain.Plaza(i, "electrico_minusvalido"));
        }
        // Plazas para motos normales
        for (int i = 19; i <= 22; i++) {
            plazas.add(new com.lksnext.parkingplantilla.domain.Plaza(i, "moto"));
        }
        // Plazas para motos minusválidos
        for (int i = 23; i <= 24; i++) {
            plazas.add(new com.lksnext.parkingplantilla.domain.Plaza(i, "moto_minusvalido"));
        }

        // Buscar reservas existentes para esa fecha y hora
        firestore.collectionGroup(reservas)
            .whereEqualTo("fecha", fecha)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<com.lksnext.parkingplantilla.domain.Reserva> reservasExistentes = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    com.lksnext.parkingplantilla.domain.Reserva reserva = doc.toObject(com.lksnext.parkingplantilla.domain.Reserva.class);
                    if (reserva != null) {
                        // Comprobar si hay solapamiento de horas
                        com.lksnext.parkingplantilla.domain.Hora horaReserva = reserva.getHoraInicio();
                        if (horaReserva != null) {
                            if ((horaReserva.getHoraInicio() <= hora.getHoraInicio() &&
                                 horaReserva.getHoraFin() > hora.getHoraInicio()) ||
                                (horaReserva.getHoraInicio() < hora.getHoraFin() &&
                                 horaReserva.getHoraFin() >= hora.getHoraFin()) ||
                                (horaReserva.getHoraInicio() >= hora.getHoraInicio() &&
                                 horaReserva.getHoraFin() <= hora.getHoraFin())) {
                                reservasExistentes.add(reserva);
                            }
                        }
                    }
                }

                // Filtrar las plazas que ya están reservadas
                for (com.lksnext.parkingplantilla.domain.Reserva reserva : reservasExistentes) {
                    com.lksnext.parkingplantilla.domain.Plaza plazaReservada = reserva.getPlazaId();
                    if (plazaReservada != null) {
                        plazas.removeIf(plaza -> plaza.getId() == plazaReservada.getId());
                    }
                }

                liveData.postValue(plazas);
            })
            .addOnFailureListener(e -> {
                android.util.Log.e(FIRESTORE_ERROR, "Error al buscar reservas existentes", e);
                liveData.postValue(plazas); // En caso de error, devolver todas las plazas
            });
    }

    // Comprobar si existe un vehículo con una matrícula determinada
    public void checkLicensePlateExists(String userId, String licensePlate, String currentVehicleId, Callback callback) {
        firestore.collection(users).document(userId)
            .collection(vehicles)
            .whereEqualTo("licensePlate", licensePlate)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                boolean exists = false;
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    // Si estamos editando, permitir la misma matrícula solo si es el mismo vehículo
                    if (currentVehicleId != null && doc.getId().equals(currentVehicleId)) continue;
                    exists = true;
                    break;
                }

                if (exists) {
                    callback.onFailure(); // La matrícula ya existe
                } else {
                    callback.onSuccess(); // La matrícula no existe o pertenece al vehículo actual
                }
            })
            .addOnFailureListener(e -> {
                android.util.Log.e(FIRESTORE_ERROR, "Error al comprobar matrícula", e);
                callback.onFailure();
            });
    }

    // Obtener el usuario actual autenticado
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    // Comprobar si hay un usuario autenticado
    public boolean isUserAuthenticated() {
        return firebaseAuth.getCurrentUser() != null;
    }

    // Obtener el ID del usuario actual
    public String getCurrentUserId() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    // Cerrar sesión de usuario
    public void signOut() {
        firebaseAuth.signOut();
    }

    // Obtener nombre de usuario desde Firestore
    public void getUsernameFromFirestore(String userId, MutableLiveData<String> usernameLiveData) {
        firestore.collection(users)
            .document(userId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    usernameLiveData.postValue(username);
                }
            })
            .addOnFailureListener(e -> {
                android.util.Log.e(FIRESTORE_ERROR, "Error al obtener nombre de usuario", e);
                usernameLiveData.postValue(null);
            });
    }

    // Añadir o actualizar vehículo
    public void addOrUpdateVehicle(Vehicle vehicle, boolean isEdit, Callback callback) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            if (callback != null) {
                callback.onFailure();
            }
            return;
        }
        String userId = user.getUid();
        firestore.collection(users).document(userId).collection(vehicles)
            .whereEqualTo("licensePlate", vehicle.getLicensePlate())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                boolean exists = false;
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    // Si estamos editando, permitir la misma matrícula solo si es el mismo vehículo
                    if (isEdit && doc.getId().equals(vehicle.getId())) continue;
                    exists = true;
                    break;
                }
                if (exists) {
                    if (callback != null) {
                        callback.onFailure();
                    }
                } else {
                    firestore.collection(users).document(userId).collection(vehicles)
                        .document(vehicle.getId())
                        .set(vehicle)
                        .addOnSuccessListener(aVoid -> {
                            if (callback != null) callback.onSuccess();
                        })
                        .addOnFailureListener(e -> {
                            if (callback != null) {
                                callback.onFailure();
                            }
                        });
                }
            })
            .addOnFailureListener(e -> {
                if (callback != null) {
                    callback.onFailure();
                }
            });
    }
}
