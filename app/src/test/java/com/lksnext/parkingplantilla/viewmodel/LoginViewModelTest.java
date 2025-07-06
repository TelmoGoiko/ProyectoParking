package com.lksnext.parkingplantilla.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.data.IVehicleRepository;
import com.lksnext.parkingplantilla.domain.Callback;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static class FakeRepository implements IVehicleRepository {
        public String expectedEmail;
        public boolean loginSuccess = true;
        public boolean findUserSuccess = true;
        @Override
        public void login(String email, String password, Callback callback) {
            if (loginSuccess) callback.onSuccess();
            else callback.onFailure();
        }
        @Override
        public void findUserByUsername(String username, Callback callback, androidx.lifecycle.MutableLiveData<String> userEmail) {
            if (findUserSuccess) {
                userEmail.setValue(expectedEmail != null ? expectedEmail : "mail@mail.com");
                callback.onSuccess();
            } else {
                callback.onFailure();
            }
        }
        // Métodos dummy para cumplir la interfaz
        @Override public void getVehicles(String userId, androidx.lifecycle.MutableLiveData<java.util.List<com.lksnext.parkingplantilla.model.Vehicle>> vehiclesLiveData) {}
        @Override public void addVehicle(String userId, com.lksnext.parkingplantilla.model.Vehicle vehicle, Callback callback) {}
        @Override public void deleteVehicle(String userId, String vehicleId, Callback callback) {}
        @Override public void addOrUpdateVehicle(com.lksnext.parkingplantilla.model.Vehicle vehicle, boolean isEdit, Callback callback) {}
        @Override public String getCurrentUserId() { return null; }
        @Override public void checkUsernameExists(String username, Callback callback) {}
        @Override public void registerUser(String username, String email, String password, Callback callback) {}
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
    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new LoginViewModel(new FakeRepository());
    }

    @Test
    public void testLoginWithEmailSuccess() {
        viewModel = new LoginViewModel(new FakeRepository() {{ loginSuccess = true; }});
        viewModel.loginUser("mail@mail.com", "1234");
        assertEquals(Boolean.TRUE, viewModel.isLogged().getValue());
    }

    @Test
    public void testLoginWithEmailFailure() {
        viewModel = new LoginViewModel(new FakeRepository() {{ loginSuccess = false; }});
        viewModel.loginUser("mail@mail.com", "1234");
        assertEquals(Boolean.FALSE, viewModel.isLogged().getValue());
    }

    @Test
    public void testLoginWithUsernameSuccess() {
        viewModel = new LoginViewModel(new FakeRepository() {{ findUserSuccess = true; expectedEmail = "mail@mail.com"; loginSuccess = true; }});
        viewModel.loginUser("usuario", "1234");
        assertEquals(Boolean.TRUE, viewModel.isLogged().getValue());
    }

    @Test
    public void testLoginWithUsernameFailure() {
        viewModel = new LoginViewModel(new FakeRepository() {{ findUserSuccess = false; }});
        viewModel.loginUser("usuario", "1234");
        assertEquals(Boolean.FALSE, viewModel.isLogged().getValue());
    }
}
