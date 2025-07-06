package com.lksnext.parkingplantilla.viewmodel;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.lifecycle.MutableLiveData;

import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.data.IVehicleRepository;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.model.Vehicle;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class MainViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private IVehicleRepository repository;
    private MainViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Usar el constructor protegido para inyectar el repositorio y el MutableLiveData mockeados
        viewModel = new MainViewModel(repository, new MutableLiveData<>());
    }

    @Test
    public void testLoadVehicles() {
        Observer<List<Vehicle>> observer = mock(Observer.class);
        viewModel.getVehiclesLiveData().observeForever(observer);
        viewModel.loadVehicles("user1");
        verify(repository).getVehicles(eq("user1"), any(MutableLiveData.class));
    }

    @Test
    public void testAddVehicle() {
        Vehicle vehicle = new Vehicle();
        Callback callback = new Callback() {
            @Override public void onSuccess() {}
            @Override public void onFailure() {}
            @Override public void onFailure(String errorMessage) {}
        };
        viewModel.addVehicle("user1", vehicle, callback);
        verify(repository).addVehicle(eq("user1"), eq(vehicle), eq(callback));
    }

    @Test
    public void testDeleteVehicle() {
        Callback callback = mock(Callback.class);
        viewModel.deleteVehicle("user1", "veh1", callback);
        verify(repository).deleteVehicle(eq("user1"), eq("veh1"), eq(callback));
    }

    @Test
    public void testAddOrUpdateVehicle() {
        Vehicle vehicle = new Vehicle();
        Callback callback = new Callback() {
            @Override public void onSuccess() {}
            @Override public void onFailure() {}
            @Override public void onFailure(String errorMessage) {}
        };
        viewModel.addOrUpdateVehicle(vehicle, false, callback);
        verify(repository).addOrUpdateVehicle(eq(vehicle), eq(false), eq(callback));
    }

    @Test
    public void testGetCurrentUserId() {
        when(repository.getCurrentUserId()).thenReturn("user1");
        assertEquals("user1", viewModel.getCurrentUserId());
    }
}
