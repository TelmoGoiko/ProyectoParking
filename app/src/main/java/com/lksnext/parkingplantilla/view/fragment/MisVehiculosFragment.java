package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.lksnext.parkingplantilla.adapter.VehicleAdapter;
import com.lksnext.parkingplantilla.databinding.FragmentMisVehiculosBinding;
import com.lksnext.parkingplantilla.model.Vehicle;
import java.util.ArrayList;
import java.util.List;

public class MisVehiculosFragment extends Fragment implements VehicleAdapter.OnVehicleClickListener {
    private FragmentMisVehiculosBinding binding;
    private VehicleAdapter adapter;
    private List<Vehicle> vehicleList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMisVehiculosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = binding.toolbar;
        if (toolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        }
        binding.recyclerViewVehiculos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VehicleAdapter(this);
        binding.recyclerViewVehiculos.setAdapter(adapter);
        cargarVehiculosDePrueba();
        binding.btnAddVehiculo.setOnClickListener(v -> {
            // Aquí iría la lógica para añadir vehículo (abrir fragmento o diálogo)
        });
    }

    private void cargarVehiculosDePrueba() {
        vehicleList.clear();
        vehicleList.add(new Vehicle("1", "Seat Ibiza", "1234ABC", "Seat", "Ibiza", Vehicle.VehicleType.CAR));
        vehicleList.add(new Vehicle("2", "Renault Clio", "5678DEF", "Renault", "Clio", Vehicle.VehicleType.CAR));
        if (adapter != null) adapter.updateVehicleList(vehicleList);
    }

    @Override
    public void onVehicleClick(Vehicle vehicle) {
        // Aquí iría la lógica para editar vehículo (abrir fragmento o diálogo)
    }

    @Override
    public void onEditClick(Vehicle vehicle) {

    }

    @Override
    public void onDeleteClick(Vehicle vehicle) {
        // Implementación vacía o lógica para eliminar vehículo
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
