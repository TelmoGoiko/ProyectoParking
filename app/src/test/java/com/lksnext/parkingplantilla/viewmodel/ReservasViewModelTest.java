package com.lksnext.parkingplantilla.viewmodel;

import static org.junit.Assert.*;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.lksnext.parkingplantilla.data.IVehicleRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.domain.Reserva;
import com.lksnext.parkingplantilla.util.IReservaNotificationScheduler;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ReservasViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static class FakeRepository implements IVehicleRepository {
        public boolean isUserAuthenticated = true;
        public String currentUserId = "user123";
        public List<Reserva> reservas = new ArrayList<>();
        public boolean createReservaSuccess = true;
        public boolean updateReservaSuccess = true;
        public boolean deleteReservaSuccess = true;
        public boolean getAvailablePlazasCalled = false;
        public boolean getReservasByUserCalled = false;
        @Override public boolean isUserAuthenticated() { return isUserAuthenticated; }
        @Override public String getCurrentUserId() { return isUserAuthenticated ? currentUserId : null; }
        @Override public void getAvailablePlazas(String fecha, Hora hora, MutableLiveData<List<Plaza>> liveData) {
            getAvailablePlazasCalled = true;
            List<Plaza> plazas = new ArrayList<>();
            plazas.add(new Plaza(1, "normal"));
            liveData.setValue(plazas);
        }
        @Override public void getReservasByUser(String userId, MutableLiveData<List<Reserva>> liveData) {
            getReservasByUserCalled = true;
            liveData.setValue(new ArrayList<>(reservas));
        }
        @Override public void createReserva(String userId, Reserva reserva, Callback callback) {
            if (createReservaSuccess) {
                reservas.add(reserva);
                callback.onSuccess();
            } else {
                callback.onFailure("Error al crear la reserva");
            }
        }
        @Override public void updateReserva(String userId, Reserva reserva, Callback callback) {
            if (updateReservaSuccess) {
                callback.onSuccess();
            } else {
                callback.onFailure("Error al actualizar la reserva");
            }
        }
        @Override public void deleteReserva(String userId, String reservaId, Callback callback) {
            if (deleteReservaSuccess) {
                callback.onSuccess();
            } else {
                callback.onFailure();
            }
        }
        @Override public void getReservaById(String userId, String reservaId, MutableLiveData<Reserva> liveData) {}
        // Métodos dummy para cumplir la interfaz
        @Override public void getVehicles(String userId, MutableLiveData<java.util.List<com.lksnext.parkingplantilla.model.Vehicle>> vehiclesLiveData) {}
        @Override public void addVehicle(String userId, com.lksnext.parkingplantilla.model.Vehicle vehicle, Callback callback) {}
        @Override public void deleteVehicle(String userId, String vehicleId, Callback callback) {}
        @Override public void addOrUpdateVehicle(com.lksnext.parkingplantilla.model.Vehicle vehicle, boolean isEdit, Callback callback) {}
        @Override public void checkUsernameExists(String username, Callback callback) {}
        @Override public void registerUser(String username, String email, String password, Callback callback) {}
        @Override public void login(String email, String pass, Callback callback) {}
        @Override public void findUserByUsername(String username, Callback callback, MutableLiveData<String> emailResult) {}
        @Override public void getUsernameFromFirestore(String userId, MutableLiveData<String> usernameLiveData) {}
        @Override public void sendPasswordResetEmail(String email, Callback callback) {}
        @Override public void signOut() {}
    }

    private static class FakeNotificationScheduler implements IReservaNotificationScheduler {
        @Override
        public void programarNotificaciones(String fecha, long horaInicio, String plaza) {
            // No hacer nada en tests
        }
    }

    private ReservasViewModel viewModel;
    private FakeRepository fakeRepository;
    private FakeNotificationScheduler fakeNotificationScheduler;

    @Before
    public void setUp() {
        fakeRepository = new FakeRepository();
        fakeNotificationScheduler = new FakeNotificationScheduler();
        Application app = new Application();
        viewModel = new ReservasViewModel(app, fakeRepository, fakeNotificationScheduler);
    }

    @Test
    public void testLoadAvailablePlazas() {
        viewModel.loadAvailablePlazas("2025-07-06", new Hora());
        assertTrue(fakeRepository.getAvailablePlazasCalled);
        assertNotNull(viewModel.getAvailablePlazas().getValue());
        assertEquals(1, viewModel.getAvailablePlazas().getValue().size());
    }

    @Test
    public void testLoadUserReservas() {
        fakeRepository.reservas.add(new Reserva());
        viewModel.loadUserReservas();
        assertTrue(fakeRepository.getReservasByUserCalled);
        assertNotNull(viewModel.getUserReservas().getValue());
        assertEquals(1, viewModel.getUserReservas().getValue().size());
    }

    @Test
    public void testCreateReservaSuccess() {
        viewModel.createReserva("2025-07-06", new Plaza(1, "normal"), new Hora(), "veh1");
        assertEquals(Boolean.TRUE, viewModel.isReservaCreated().getValue());
        assertNull(viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testCreateReservaFailure() {
        fakeRepository.createReservaSuccess = false;
        viewModel.createReserva("2025-07-06", new Plaza(1, "normal"), new Hora(), "veh1");
        assertEquals(Boolean.FALSE, viewModel.isReservaCreated().getValue());
        assertEquals("Error al crear la reserva", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testUpdateReservaSuccess() {
        Reserva reserva = new Reserva();
        reserva.setEstado("Confirmada");
        viewModel.updateReserva(reserva);
        assertEquals(Boolean.TRUE, viewModel.isReservaUpdated().getValue());
    }

    @Test
    public void testUpdateReservaFailure() {
        fakeRepository.updateReservaSuccess = false;
        Reserva reserva = new Reserva();
        reserva.setEstado("Confirmada");
        viewModel.updateReserva(reserva);
        assertEquals(Boolean.FALSE, viewModel.isReservaUpdated().getValue());
        assertEquals("Error al actualizar la reserva", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testEliminarReservaSuccess() {
        viewModel.eliminarReserva("res1");
        assertEquals(Boolean.TRUE, viewModel.isReservaDeleted().getValue());
    }

    @Test
    public void testEliminarReservaFailure() {
        fakeRepository.deleteReservaSuccess = false;
        viewModel.eliminarReserva("res1");
        assertEquals(Boolean.FALSE, viewModel.isReservaDeleted().getValue());
        assertEquals("Error al eliminar la reserva", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testUpdateReservaEstadoNoConfirmada() {
        Reserva reservaPendiente = new Reserva();
        reservaPendiente.setEstado("pendiente");
        viewModel.updateReserva(reservaPendiente);
        assertEquals(Boolean.FALSE, viewModel.isReservaUpdated().getValue());
        assertEquals("Solo se puede editar una reserva confirmada.", viewModel.getErrorMessage().getValue());

        Reserva reservaEnCurso = new Reserva();
        reservaEnCurso.setEstado("en curso");
        viewModel.updateReserva(reservaEnCurso);
        assertEquals(Boolean.FALSE, viewModel.isReservaUpdated().getValue());
        assertEquals("Solo se puede editar una reserva confirmada.", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testCreateReservaNoUser() {
        fakeRepository.isUserAuthenticated = false;
        viewModel.createReserva("2025-07-06", new Plaza(1, "normal"), new Hora(), "veh1");
        assertEquals(Boolean.FALSE, viewModel.isReservaCreated().getValue());
        assertEquals("No hay usuario autenticado", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testNoPermitirReservaSolapadaVehiculo() {
        fakeRepository.createReservaSuccess = false;
        // Simula que el repositorio detecta solapamiento
        viewModel.createReserva("2025-07-06", new Plaza(1, "normal"), new Hora(3600, 7200), "veh1");
        assertEquals(Boolean.FALSE, viewModel.isReservaCreated().getValue());
        assertEquals("Error al crear la reserva", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testNoPermitirReservaDuracionMayor8Horas() {
        fakeRepository.createReservaSuccess = false;
        // Simula que el repositorio detecta duración > 8h
        viewModel.createReserva("2025-07-06", new Plaza(1, "normal"), new Hora(0, 9 * 3600), "veh1");
        assertEquals(Boolean.FALSE, viewModel.isReservaCreated().getValue());
        assertEquals("Error al crear la reserva", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testNoPermitirReservaFueraDeRango7Dias() {
        fakeRepository.createReservaSuccess = false;
        // Simula que el repositorio detecta fecha fuera de rango
        viewModel.createReserva("2025-08-20", new Plaza(1, "normal"), new Hora(0, 3600), "veh1");
        assertEquals(Boolean.FALSE, viewModel.isReservaCreated().getValue());
        assertEquals("Error al crear la reserva", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testCrearReservaVehiculoInexistente() {
        fakeRepository.createReservaSuccess = false;
        viewModel.createReserva("2025-07-06", new Plaza(1, "normal"), new Hora(0, 3600), "vehiculo_inexistente");
        assertEquals(Boolean.FALSE, viewModel.isReservaCreated().getValue());
        assertEquals("Error al crear la reserva", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testCrearReservaPlazaInexistente() {
        fakeRepository.createReservaSuccess = false;
        viewModel.createReserva("2025-07-06", null, new Hora(0, 3600), "veh1");
        assertEquals(Boolean.FALSE, viewModel.isReservaCreated().getValue());
        assertEquals("Error al crear la reserva", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testCrearReservaHoraInicioIgualFin() {
        fakeRepository.createReservaSuccess = false;
        viewModel.createReserva("2025-07-06", new Plaza(1, "normal"), new Hora(3600, 3600), "veh1");
        assertEquals(Boolean.FALSE, viewModel.isReservaCreated().getValue());
        assertEquals("Error al crear la reserva", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testCrearReservaHoraInicioMayorFin() {
        fakeRepository.createReservaSuccess = false;
        viewModel.createReserva("2025-07-06", new Plaza(1, "normal"), new Hora(7200, 3600), "veh1");
        assertEquals(Boolean.FALSE, viewModel.isReservaCreated().getValue());
        assertEquals("Error al crear la reserva", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testCrearReservaCamposNulos() {
        fakeRepository.createReservaSuccess = false;
        viewModel.createReserva(null, null, null, null);
        assertEquals(Boolean.FALSE, viewModel.isReservaCreated().getValue());
        assertEquals("Error al crear la reserva", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testEliminarReservaInexistente() {
        fakeRepository.deleteReservaSuccess = false;
        viewModel.eliminarReserva("reserva_inexistente");
        assertEquals(Boolean.FALSE, viewModel.isReservaDeleted().getValue());
        assertEquals("Error al eliminar la reserva", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testActualizarReservaInexistente() {
        fakeRepository.updateReservaSuccess = false;
        Reserva reserva = new Reserva();
        reserva.setEstado("Confirmada");
        viewModel.updateReserva(reserva);
        assertEquals(Boolean.FALSE, viewModel.isReservaUpdated().getValue());
        assertEquals("Error al actualizar la reserva", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testLimpiarErrorTrasExito() {
        fakeRepository.createReservaSuccess = false;
        viewModel.createReserva("2025-07-06", new Plaza(1, "normal"), new Hora(0, 3600), "veh1");
        assertEquals(Boolean.FALSE, viewModel.isReservaCreated().getValue());
        assertEquals("Error al crear la reserva", viewModel.getErrorMessage().getValue());
        fakeRepository.createReservaSuccess = true;
        viewModel.createReserva("2025-07-07", new Plaza(1, "normal"), new Hora(0, 3600), "veh1");
        assertEquals(Boolean.TRUE, viewModel.isReservaCreated().getValue());
        assertNull(viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testLimpiarErrorEliminarTrasExito() {
        fakeRepository.deleteReservaSuccess = false;
        viewModel.eliminarReserva("reserva_inexistente");
        assertEquals(Boolean.FALSE, viewModel.isReservaDeleted().getValue());
        assertEquals("Error al eliminar la reserva", viewModel.getErrorMessage().getValue());
        fakeRepository.deleteReservaSuccess = true;
        viewModel.eliminarReserva("reserva_existente");
        assertEquals(Boolean.TRUE, viewModel.isReservaDeleted().getValue());
        assertNull(viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testLimpiarErrorActualizarTrasExito() {
        fakeRepository.updateReservaSuccess = false;
        Reserva reserva = new Reserva();
        reserva.setEstado("Confirmada");
        viewModel.updateReserva(reserva);
        assertEquals(Boolean.FALSE, viewModel.isReservaUpdated().getValue());
        assertEquals("Error al actualizar la reserva", viewModel.getErrorMessage().getValue());
        fakeRepository.updateReservaSuccess = true;
        viewModel.updateReserva(reserva);
        assertEquals(Boolean.TRUE, viewModel.isReservaUpdated().getValue());
        assertNull(viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testEliminarReservaSinUsuario() {
        fakeRepository.isUserAuthenticated = false;
        viewModel.eliminarReserva("res1");
        assertEquals(Boolean.FALSE, viewModel.isReservaDeleted().getValue());
        assertEquals("No hay usuario autenticado", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testActualizarReservaSinUsuario() {
        fakeRepository.isUserAuthenticated = false;
        Reserva reserva = new Reserva();
        reserva.setEstado("Confirmada");
        viewModel.updateReserva(reserva);
        assertEquals(Boolean.FALSE, viewModel.isReservaUpdated().getValue());
        assertEquals("No hay usuario autenticado", viewModel.getErrorMessage().getValue());
    }

    @Test
    public void testEliminarReservaIdNulo() {
        fakeRepository.isUserAuthenticated = true;
        viewModel.eliminarReserva(null);
        // El fakeRepository ignora el id, pero en un repo real debería manejarse el error
        // Aquí solo comprobamos que no crashea y que el flujo de éxito/fallo se ejecuta
        assertNotNull(viewModel.isReservaDeleted().getValue());
    }
}
