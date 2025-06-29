package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.FragmentAnadirEditarVehiculoBinding;
import com.lksnext.parkingplantilla.model.Vehicle;
import com.lksnext.parkingplantilla.viewmodel.MainViewModel;
import com.lksnext.parkingplantilla.domain.Callback;
import com.google.firebase.auth.FirebaseAuth;
import java.util.UUID;

public class AnadirEditarVehiculoFragment extends Fragment {
    private FragmentAnadirEditarVehiculoBinding binding;
    private MainViewModel viewModel;
    private Vehicle vehicleToEdit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAnadirEditarVehiculoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Use the shared Toolbar from MainActivity
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(vehicleToEdit == null ? "Añadir Vehículo" : "Editar Vehículo");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        requireActivity().findViewById(R.id.mainToolbar).setOnClickListener(v -> requireActivity().onBackPressed());
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Spinner de tipo de vehículo
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new String[]{
                Vehicle.VehicleType.CAR.getDisplayName(),
                Vehicle.VehicleType.MOTORCYCLE.getDisplayName(),
                Vehicle.VehicleType.VAN.getDisplayName()
        });
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTipo.setAdapter(tipoAdapter);

        // Si recibimos un vehículo para editar, rellenar campos
        if (getArguments() != null && getArguments().containsKey("vehicle")) {
            vehicleToEdit = (Vehicle) getArguments().getSerializable("vehicle");
            if (vehicleToEdit != null) {
                binding.tvTitulo.setText("Editar Vehículo");
                binding.etNombre.setText(vehicleToEdit.getName());
                binding.etMatricula.setText(vehicleToEdit.getLicensePlate());
                binding.etMarca.setText(vehicleToEdit.getBrand());
                binding.etModelo.setText(vehicleToEdit.getModel());
                binding.spinnerTipo.setSelection(vehicleToEdit.getType().ordinal());
                binding.switchElectric.setChecked(vehicleToEdit.isElectric());
            }
        }

        binding.btnGuardar.setOnClickListener(v -> guardarVehiculo());
    }

    private void guardarVehiculo() {
        String nombre = binding.etNombre.getText().toString().trim();
        String matricula = binding.etMatricula.getText().toString().trim();
        String marca = binding.etMarca.getText().toString().trim();
        String modelo = binding.etModelo.getText().toString().trim();
        int tipoPos = binding.spinnerTipo.getSelectedItemPosition();
        Vehicle.VehicleType tipo = Vehicle.VehicleType.values()[tipoPos];
        boolean isElectric = binding.switchElectric.isChecked();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(matricula) || TextUtils.isEmpty(marca) || TextUtils.isEmpty(modelo)) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String id = (vehicleToEdit != null) ? vehicleToEdit.getId() : UUID.randomUUID().toString();
        Vehicle vehiculo = new Vehicle(id, nombre, matricula, marca, modelo, tipo, isElectric);

        viewModel.addVehicle(userId, vehiculo, new Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Vehículo guardado", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
            @Override
            public void onFailure() {
                Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
