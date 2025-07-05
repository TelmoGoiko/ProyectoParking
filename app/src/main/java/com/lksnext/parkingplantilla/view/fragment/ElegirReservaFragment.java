package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.lksnext.parkingplantilla.databinding.FragmentElegirReservaBinding;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.model.Vehicle;
import com.lksnext.parkingplantilla.viewmodel.MainViewModel;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

public class ElegirReservaFragment extends Fragment {
    private FragmentElegirReservaBinding binding;
    private MainViewModel viewModel;
    private ArrayList<Vehicle> vehicleList = new ArrayList<>();
    private Vehicle selectedVehicle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentElegirReservaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Use the shared Toolbar from MainActivity
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Elegir tipo de reserva");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        requireActivity().findViewById(R.id.mainToolbar).setOnClickListener(v -> requireActivity().onBackPressed());

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewModel.loadVehicles(userId);
        viewModel.getVehiclesLiveData().observe(getViewLifecycleOwner(), vehicles -> {
            vehicleList.clear();
            if (vehicles != null) vehicleList.addAll(vehicles);
            ArrayList<String> nombres = new ArrayList<>();
            for (Vehicle v : vehicleList) {
                nombres.add(v.getName() + " (" + v.getLicensePlate() + ")");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, nombres);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerVehiculos.setAdapter(adapter);
        });
        binding.spinnerVehiculos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < vehicleList.size()) {
                    selectedVehicle = vehicleList.get(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedVehicle = null;
            }
        });
        // Adaptación: ahora los "cuadrados" son opcionReservar y opcionAparcarYa
        binding.opcionReservar.setOnClickListener(v -> {
            if (selectedVehicle == null) {
                Toast.makeText(getContext(), "Selecciona un vehículo", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle args = new Bundle();
            args.putSerializable("selectedVehicle", selectedVehicle);
            Navigation.findNavController(view).navigate(
                com.lksnext.parkingplantilla.R.id.action_elegirReservaFragment_to_reservarFragment, args);
        });
        binding.opcionAparcarYa.setOnClickListener(v -> {
            if (selectedVehicle == null) {
                Toast.makeText(getContext(), "Selecciona un vehículo", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle args = new Bundle();
            args.putSerializable("selectedVehicle", selectedVehicle);
            Navigation.findNavController(view).navigate(
                com.lksnext.parkingplantilla.R.id.action_elegirReservaFragment_to_aparcarYaFragment, args);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
