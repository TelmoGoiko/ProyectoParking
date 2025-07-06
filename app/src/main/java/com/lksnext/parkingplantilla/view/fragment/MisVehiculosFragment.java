package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.adapter.VehicleAdapter;
import com.lksnext.parkingplantilla.databinding.FragmentMisVehiculosBinding;
import com.lksnext.parkingplantilla.model.Vehicle;
import com.lksnext.parkingplantilla.viewmodel.MainViewModel;
import java.util.ArrayList;
import java.util.List;

public class MisVehiculosFragment extends Fragment implements VehicleAdapter.OnVehicleClickListener {
    private FragmentMisVehiculosBinding binding;
    private VehicleAdapter adapter;
    private List<Vehicle> vehicleList = new ArrayList<>();
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMisVehiculosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(getString(R.string.mis_vehiculos));
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        requireActivity().findViewById(R.id.mainToolbar).setOnClickListener(v ->
            requireActivity().getOnBackPressedDispatcher().onBackPressed());

        binding.recyclerViewVehiculos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VehicleAdapter(this);
        binding.recyclerViewVehiculos.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Cargar vehículos del usuario actual
        cargarVehiculos();

        viewModel.getVehiclesLiveData().observe(getViewLifecycleOwner(), vehicles -> {
            vehicleList.clear();
            if (vehicles != null) vehicleList.addAll(vehicles);
            adapter.setVehicles(new ArrayList<>(vehicleList));
            binding.tvEmptyVehiculos.setVisibility(vehicleList.isEmpty() ? View.VISIBLE : View.GONE);
        });

        binding.btnAddVehiculo.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(
                com.lksnext.parkingplantilla.R.id.action_misVehiculosFragment_to_anadirEditarVehiculoFragment);
        });
    }

    private void cargarVehiculos() {
        // Obtener userId del ViewModel (el ViewModel obtiene el ID del repository)
        String userId = viewModel.getCurrentUserId();
        if (userId != null) {
            viewModel.loadVehicles(userId);
        } else {
            Toast.makeText(getContext(), getString(R.string.no_usuario_autenticado), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onVehicleClick(Vehicle vehicle) {
        Bundle args = new Bundle();
        args.putSerializable("vehicle", vehicle);
        Navigation.findNavController(requireView()).navigate(
            com.lksnext.parkingplantilla.R.id.action_misVehiculosFragment_to_anadirEditarVehiculoFragment, args);
        // Recargar al volver de editar
        requireActivity().getSupportFragmentManager().addOnBackStackChangedListener(() -> cargarVehiculos());
    }

    @Override
    public void onEditClick(Vehicle vehicle) {
        Bundle args = new Bundle();
        args.putSerializable("vehicle", vehicle);
        Navigation.findNavController(requireView()).navigate(
            com.lksnext.parkingplantilla.R.id.action_misVehiculosFragment_to_anadirEditarVehiculoFragment, args);
        // Recargar al volver de editar
        requireActivity().getSupportFragmentManager().addOnBackStackChangedListener(() -> cargarVehiculos());
    }

    public void onDeleteClick(Vehicle vehicle) {
        DeleteVehicleDialogFragment dialog = DeleteVehicleDialogFragment.newInstance(vehicle);
        dialog.setDeleteVehicleListener(v -> eliminarVehiculo(v));
        dialog.show(getParentFragmentManager(), "DeleteVehicleDialog");
    }

    private void eliminarVehiculo(Vehicle vehicle) {
        String userId = viewModel.getCurrentUserId();
        if (userId != null) {
            viewModel.deleteVehicle(userId, vehicle.getId(), new com.lksnext.parkingplantilla.domain.Callback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), getString(R.string.vehiculo_eliminado), Toast.LENGTH_SHORT).show();
                    cargarVehiculos();
                }

                @Override
                public void onFailure() {
                    Toast.makeText(getContext(), getString(R.string.error_eliminar_vehiculo), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), getString(R.string.no_usuario_autenticado), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
