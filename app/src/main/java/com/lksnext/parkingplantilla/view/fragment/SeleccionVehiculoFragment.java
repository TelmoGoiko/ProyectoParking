package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.FragmentSeleccionVehiculoBinding;

public class SeleccionVehiculoFragment extends Fragment {
    private FragmentSeleccionVehiculoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSeleccionVehiculoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar la barra de herramientas
        binding.toolbarSeleccionVehiculo.setNavigationOnClickListener(v -> {
            // Volver atrás al presionar la flecha de navegación
            requireActivity().onBackPressed();
        });

        // Configurar el botón de continuar para navegar a la pantalla de elegir plaza
        binding.btnContinuar.setOnClickListener(v -> {
            // Navegar a la pantalla de elegir plaza
            Navigation.findNavController(view).navigate(R.id.action_seleccionVehiculoFragment_to_elegirPlazaFragment);
        });

        // Configurar el botón de agregar vehículo para navegar a la pantalla de vehículos
        binding.btnAgregarVehiculo.setOnClickListener(v -> {
            // Navegar a la pantalla de mis vehículos
            Navigation.findNavController(view).navigate(R.id.action_seleccionVehiculoFragment_to_misVehiculosFragment);
        });

        // Aquí se cargarían los vehículos del usuario desde la base de datos
        // Por ahora, simulamos que no hay vehículos para mostrar el mensaje
        mostrarVehiculosDisponibles(false);
    }

    private void mostrarVehiculosDisponibles(boolean hayVehiculos) {
        if (hayVehiculos) {
            binding.recyclerViewVehiculos.setVisibility(View.VISIBLE);
            binding.tvNoVehiculos.setVisibility(View.GONE);
        } else {
            binding.recyclerViewVehiculos.setVisibility(View.GONE);
            binding.tvNoVehiculos.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
