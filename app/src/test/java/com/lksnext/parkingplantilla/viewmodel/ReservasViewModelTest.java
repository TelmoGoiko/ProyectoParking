package com.lksnext.parkingplantilla.viewmodel;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.domain.Reserva;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

public class ReservasViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private DataRepository repository;
    @Mock
    private Application application;

    private ReservasViewModel viewModel;

    // Constructor alternativo para inyectar el repositorio mockeado
    static class TestableReservasViewModel extends ReservasViewModel {
        public TestableReservasViewModel(Application app, DataRepository repo) {
            super(app);
            // Sobrescribe el repositorio real por el mock
            try {
                java.lang.reflect.Field f = ReservasViewModel.class.getDeclaredField("repository");
                f.setAccessible(true);
                f.set(this, repo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = mock(DataRepository.class);
        viewModel = new TestableReservasViewModel(application, repository);
    }

    @Test
    public void testLoadAvailablePlazas() {
        Observer<List<Plaza>> observer = mock(Observer.class);
        viewModel.getAvailablePlazas().observeForever(observer);
        viewModel.loadAvailablePlazas("01/01/2025", new Hora(3600, 7200));
        verify(repository).getAvailablePlazas(eq("01/01/2025"), any(Hora.class), any());
    }

    @Test
    public void testLoadUserReservas() {
        Observer<List<Reserva>> observer = mock(Observer.class);
        viewModel.getUserReservas().observeForever(observer);
        when(repository.getCurrentUserId()).thenReturn("user1");
        viewModel.loadUserReservas();
        verify(repository).getReservasByUser(eq("user1"), any());
    }

    @Test
    public void testCreateReservaSuccess() {
        when(repository.getCurrentUserId()).thenReturn("user1");
        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(2);
            cb.onSuccess();
            return null;
        }).when(repository).createReserva(anyString(), any(Reserva.class), any(Callback.class));
        Observer<Boolean> observer = mock(Observer.class);
        viewModel.isReservaCreated().observeForever(observer);
        viewModel.createReserva("01/01/2025", new Plaza(1, "normal"), new Hora(3600, 7200), "veh1");
        assertTrue(viewModel.isReservaCreated().getValue());
    }

    @Test
    public void testCreateReservaFailure() {
        when(repository.getCurrentUserId()).thenReturn("user1");
        doAnswer(invocation -> {
            Callback cb = invocation.getArgument(2);
            cb.onFailure("Error");
            return null;
        }).when(repository).createReserva(anyString(), any(Reserva.class), any(Callback.class));
        Observer<Boolean> observer = mock(Observer.class);
        viewModel.isReservaCreated().observeForever(observer);
        viewModel.createReserva("01/01/2025", new Plaza(1, "normal"), new Hora(3600, 7200), "veh1");
        assertFalse(viewModel.isReservaCreated().getValue());
        assertEquals("Error", viewModel.getErrorMessage().getValue());
    }

    // Puedes seguir con tests similares para updateReserva, eliminarReserva, getReservaById, etc.
}
