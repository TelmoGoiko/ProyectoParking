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
import com.lksnext.parkingplantilla.databinding.FragmentCheckoutBinding;

public class CheckoutFragment extends Fragment {
    private FragmentCheckoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar la barra de herramientas
        binding.toolbarCheckout.setNavigationOnClickListener(v -> {
            // Volver atrás al presionar la flecha de navegación
            requireActivity().onBackPressed();
        });

        // Configurar los datos del vehículo
        configurarDatosVehiculo();

        // Configurar los datos del lugar y plaza
        configurarDatosLugar();

        // Configurar las fechas y horas
        configurarFechasHoras();

        // Configurar el botón de aplicar descuento
        binding.btnAplicarDescuento.setOnClickListener(v -> {
            String codigoDescuento = binding.etCodigoDescuento.getText().toString();
            if (!codigoDescuento.isEmpty()) {
                // Simular aplicación de descuento
                binding.tvDescuentoAplicado.setText("Descuento PARKING10 aplicado: 10%");
                binding.tvImporteDescuento.setText("-0.60€");
                binding.tvImporteTotal.setText("5.40€");
                Toast.makeText(requireContext(), "Descuento aplicado correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Introduce un código de descuento", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar el botón de completar reserva
        binding.btnCompletarReserva.setOnClickListener(v -> {
            // Simular que la reserva se ha completado correctamente
            Toast.makeText(requireContext(), "¡Reserva completada con éxito!", Toast.LENGTH_LONG).show();

            // Navegar a la pantalla de mis reservas
            Navigation.findNavController(view).navigate(R.id.action_checkoutFragment_to_misReservasFragment);
        });
    }

    private void configurarDatosVehiculo() {
        // Aquí se configurarían los datos del vehículo con datos reales
        // Por ahora usamos datos de ejemplo
        binding.tvModeloVehiculo.setText("Renault Clio");
        binding.tvMatriculaVehiculo.setText("1234 ABC");
    }

    private void configurarDatosLugar() {
        // Aquí se configurarían los datos del lugar y plaza con datos reales
        // Por ahora usamos datos de ejemplo
        binding.tvNombreParking.setText("Parking Central");
        binding.tvDireccionParking.setText("Calle Principal, 123");
        binding.tvNumeroPlaza.setText("Plaza: A-15");
    }

    private void configurarFechasHoras() {
        // Aquí se configurarían las fechas y horas con datos reales
        // Por ahora usamos datos de ejemplo
        binding.tvFechaEntrada.setText("Entrada: 18/06/2025 10:00");
        binding.tvFechaSalida.setText("Salida: 18/06/2025 12:00");
        binding.tvDuracion.setText("Duración: 2 horas");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
