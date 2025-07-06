package com.lksnext.parkingplantilla.viewmodel;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.lksnext.parkingplantilla.data.IVehicleRepository;
import com.lksnext.parkingplantilla.domain.Callback;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ProfileViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static class FakeRepository implements IVehicleRepository {
        public boolean isUserAuthenticated = true;
        public String currentUserId = "user123";
        public String userEmail = "mail@mail.com";
        public String userName = "usuario";
        public boolean resetSuccess = true;
        public boolean signOutCalled = false;
        @Override public boolean isUserAuthenticated() { return isUserAuthenticated; }
        @Override public String getCurrentUserId() { return currentUserId; }
        @Override public void getUsernameFromFirestore(String userId, MutableLiveData<String> usernameLiveData) {
            usernameLiveData.setValue(userName);
        }
        @Override public void sendPasswordResetEmail(String email, Callback callback) {
            if (resetSuccess) callback.onSuccess();
            else callback.onFailure();
        }
        @Override public void signOut() { signOutCalled = true; }
        // Métodos dummy para cumplir la interfaz
        @Override public void getVehicles(String userId, MutableLiveData<java.util.List<com.lksnext.parkingplantilla.model.Vehicle>> vehiclesLiveData) {}
        @Override public void addVehicle(String userId, com.lksnext.parkingplantilla.model.Vehicle vehicle, Callback callback) {}
        @Override public void deleteVehicle(String userId, String vehicleId, Callback callback) {}
        @Override public void addOrUpdateVehicle(com.lksnext.parkingplantilla.model.Vehicle vehicle, boolean isEdit, Callback callback) {}
        @Override public void checkUsernameExists(String username, Callback callback) {}
        @Override public void registerUser(String username, String email, String password, Callback callback) {}
        @Override public void login(String email, String pass, Callback callback) {}
        @Override public void findUserByUsername(String username, Callback callback, MutableLiveData<String> emailResult) {}
        @Override public void deleteReserva(String userId, String reservaId, Callback callback) {}
        @Override public void updateReserva(String userId, com.lksnext.parkingplantilla.domain.Reserva reserva, Callback callback) {}
        @Override public void createReserva(String userId, com.lksnext.parkingplantilla.domain.Reserva reserva, Callback callback) {}
        @Override public void getReservaById(String userId, String reservaId, MutableLiveData<com.lksnext.parkingplantilla.domain.Reserva> liveData) {}
        @Override public void getReservasByUser(String userId, MutableLiveData<java.util.List<com.lksnext.parkingplantilla.domain.Reserva>> liveData) {}
        @Override public void getAvailablePlazas(String fecha, com.lksnext.parkingplantilla.domain.Hora hora, MutableLiveData<java.util.List<com.lksnext.parkingplantilla.domain.Plaza>> liveData) {}
    }
    private ProfileViewModel viewModel;
    private FakeRepository fakeRepository;

    @Before
    public void setUp() {
        fakeRepository = new FakeRepository();
        viewModel = new ProfileViewModel(fakeRepository);
    }

    @Test
    public void testLoadUserData_authenticated() {
        fakeRepository.isUserAuthenticated = true;
        fakeRepository.userName = "usuario";
        viewModel.loadUserData();
        assertEquals("usuario", viewModel.getUserName().getValue());
    }

    @Test
    public void testLoadUserData_notAuthenticated() {
        fakeRepository.isUserAuthenticated = false;
        viewModel.loadUserData();
        assertNull(viewModel.getUserName().getValue());
    }

    @Test
    public void testSendPasswordResetEmail_success() {
        fakeRepository.resetSuccess = true;
        viewModel.sendPasswordResetEmail("mail@mail.com");
        assertEquals("Correo de recuperación enviado", viewModel.getPasswordResetResult().getValue());
    }

    @Test
    public void testSendPasswordResetEmail_failure() {
        fakeRepository.resetSuccess = false;
        viewModel.sendPasswordResetEmail("mail@mail.com");
        assertEquals("Error al enviar el correo de recuperación", viewModel.getPasswordResetResult().getValue());
    }

    @Test
    public void testSignOut() {
        viewModel.signOut();
        assertTrue(fakeRepository.signOutCalled);
    }
}
