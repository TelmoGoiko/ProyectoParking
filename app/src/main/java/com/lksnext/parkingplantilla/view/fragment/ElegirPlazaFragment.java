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
import com.lksnext.parkingplantilla.databinding.FragmentElegirPlazaBinding;

public class ElegirPlazaFragment extends Fragment {
    private FragmentElegirPlazaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentElegirPlazaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar la barra de herramientas
        binding.toolbarElegirPlaza.setNavigationOnClickListener(v -> {
            // Volver atrás al presionar la flecha de navegación
            requireActivity().onBackPressed();
        });

        // Configurar el mapa (en una implementación real, aquí se mostraría un mapa del parking)
        configurarMapaParking();

        // Configurar los detalles de la reserva
        configurarDetallesReserva();

        // Configurar el click en la sección de fecha/hora (volver a pantalla de reserva)
        binding.contenedorFechaHora.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Volviendo a la pantalla de reserva", Toast.LENGTH_SHORT).show();
            // Navegar de vuelta al fragmento de reserva
            Navigation.findNavController(view).navigate(R.id.action_elegirPlazaFragment_to_reservarFragment);
        });

        // Configurar el click en la sección de vehículo (volver a pantalla de selección de vehículo)
        binding.contenedorVehiculo.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Volviendo a la pantalla de selección de vehículo", Toast.LENGTH_SHORT).show();
            // Navegar de vuelta al fragmento de selección de vehículo
            Navigation.findNavController(view).navigate(R.id.action_elegirPlazaFragment_to_seleccionVehiculoFragment);
        });

        // Configurar el botón de continuar a pago
        binding.btnContinuar.setOnClickListener(v -> {
            // Comprobar si se ha seleccionado una plaza (en una implementación real)
            Toast.makeText(requireContext(), "Continuando al pago con la plaza A-15", Toast.LENGTH_SHORT).show();
            // Navegar a la pantalla de checkout
            Navigation.findNavController(view).navigate(R.id.action_elegirPlazaFragment_to_checkoutFragment);
        });

        // Simular selección de plaza (en este caso con un click en el mapa)
        binding.contenedorMapa.setOnClickListener(v -> {
            // Simular que se ha seleccionado una plaza
            Toast.makeText(requireContext(), "Plaza A-15 seleccionada", Toast.LENGTH_SHORT).show();
        });
    }

    private void configurarMapaParking() {
        // Aquí se configuraría el mapa del parking
        // Por ahora solo mostramos un mensaje
        Toast.makeText(requireContext(), "Seleccione una plaza disponible", Toast.LENGTH_SHORT).show();
    }

    private void configurarDetallesReserva() {
        // Aquí se configurarían los detalles de la reserva con datos reales
        // Por ahora usamos datos de ejemplo
        binding.tvFechaHoraEntrada.setText("Entrada: 18/06/2025 10:00");
        binding.tvFechaHoraSalida.setText("Salida: 18/06/2025 12:00");
        binding.tvVehiculo.setText("Vehículo: Renault Clio (1234 ABC)");
        binding.tvLugar.setText("Parking: Central - Nivel 1");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
