package com.lksnext.parkingplantilla.viewmodel;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.data.IVehicleRepository;
import com.lksnext.parkingplantilla.domain.Callback;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class RegisterViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static class FakeRepository implements IVehicleRepository {
        @Override
        public void checkUsernameExists(String username, Callback callback) {
            // No hace nada
        }
        @Override
        public void registerUser(String username, String email, String password, Callback callback) {
            // No hace nada
        }
        @Override
        public void addVehicle(String userId, com.lksnext.parkingplantilla.model.Vehicle vehicle, Callback callback) {}
        @Override
        public void deleteVehicle(String userId, String vehicleId, Callback callback) {}
        @Override
        public void addOrUpdateVehicle(com.lksnext.parkingplantilla.model.Vehicle vehicle, boolean isEdit, Callback callback) {}
        @Override
        public String getCurrentUserId() { return null; }
        @Override
        public void getVehicles(String userId, androidx.lifecycle.MutableLiveData<java.util.List<com.lksnext.parkingplantilla.model.Vehicle>> vehiclesLiveData) {}
        @Override public void login(String email, String pass, Callback callback) {}
        @Override public void findUserByUsername(String username, Callback callback, androidx.lifecycle.MutableLiveData<String> emailResult) {}
        @Override public void signOut() {}
        @Override public void sendPasswordResetEmail(String email, Callback callback) {}
        @Override public void getUsernameFromFirestore(String userId, androidx.lifecycle.MutableLiveData<String> usernameLiveData) {}
        @Override public boolean isUserAuthenticated() { return true; }
        @Override public void deleteReserva(String userId, String reservaId, Callback callback) {}
        @Override public void updateReserva(String userId, com.lksnext.parkingplantilla.domain.Reserva reserva, Callback callback) {}
        @Override public void createReserva(String userId, com.lksnext.parkingplantilla.domain.Reserva reserva, Callback callback) {}
        @Override public void getReservaById(String userId, String reservaId, MutableLiveData<com.lksnext.parkingplantilla.domain.Reserva> liveData) {}
        @Override public void getReservasByUser(String userId, MutableLiveData<java.util.List<com.lksnext.parkingplantilla.domain.Reserva>> liveData) {}
        @Override public void getAvailablePlazas(String fecha, com.lksnext.parkingplantilla.domain.Hora hora, MutableLiveData<java.util.List<com.lksnext.parkingplantilla.domain.Plaza>> liveData) {}
    }
    private RegisterViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new RegisterViewModel(new FakeRepository());
    }

    @Test
    public void testPasswordMismatch() {
        viewModel.registerUser("user", "mail@mail.com", "1234", "4321");
        assertEquals(false, viewModel.isRegistered().getValue());
        assertEquals("Las contraseñas no coinciden", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testUsernameAlreadyExists() {
        RegisterViewModel viewModel = new RegisterViewModel(new FakeRepository() {
            @Override
            public void checkUsernameExists(String username, Callback callback) {
                callback.onFailure(); // Simula usuario ya existente
            }
        });
        viewModel.registerUser("user", "mail@mail.com", "1234", "1234");
        assertEquals(false, viewModel.isRegistered().getValue());
        assertEquals("El nombre de usuario ya está en uso", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testRegisterSuccess() {
        RegisterViewModel viewModel = new RegisterViewModel(new FakeRepository() {
            @Override
            public void checkUsernameExists(String username, Callback callback) {
                callback.onSuccess(); // Usuario no existe
            }
            @Override
            public void registerUser(String username, String email, String password, Callback callback) {
                callback.onSuccess(); // Registro exitoso
            }
        });
        viewModel.registerUser("user", "mail@mail.com", "1234", "1234");
        assertEquals(true, viewModel.isRegistered().getValue());
        assertNull(viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testRegisterEmailError() {
        RegisterViewModel viewModel = new RegisterViewModel(new FakeRepository() {
            @Override
            public void checkUsernameExists(String username, Callback callback) {
                callback.onSuccess(); // Usuario no existe
            }
            @Override
            public void registerUser(String username, String email, String password, Callback callback) {
                callback.onFailure(); // Error de email
            }
        });
        viewModel.registerUser("user", "mail@mail.com", "1234", "1234");
        assertEquals(false, viewModel.isRegistered().getValue());
        assertEquals("Error al crear la cuenta. El correo electrónico puede estar ya en uso.", viewModel.getErrorMessage().getValue());
    }
}
