package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lksnext.parkingplantilla.adapter.ReservasAdapter;
import com.lksnext.parkingplantilla.databinding.FragmentMisReservasBinding;
import com.lksnext.parkingplantilla.model.Reserva;

import java.util.ArrayList;
import java.util.List;

public class MisReservasFragment extends Fragment {

    private FragmentMisReservasBinding binding;
    private ReservasAdapter reservasAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMisReservasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Configurar el RecyclerView
        binding.recyclerViewReservas.setLayoutManager(new LinearLayoutManager(getContext()));
        cargarDatosDePrueba();

        // Configurar la navegación con la flecha de retroceso
        binding.toolbar.setNavigationOnClickListener(v -> {
            // Retroceder a la pantalla anterior
            requireActivity().onBackPressed();
        });
    }

    private void cargarDatosDePrueba() {
        List<Reserva> reservas = new ArrayList<>();
        reservas.add(new Reserva("Parking Centro", "12/06/2025", "14:00", "16:00", "Confirmada"));
        reservas.add(new Reserva("Parking Norte", "15/06/2025", "09:00", "11:00", "Pendiente"));
        reservas.add(new Reserva("Parking Sur", "20/06/2025", "18:00", "20:00", "Confirmada"));
        reservasAdapter = new ReservasAdapter(reservas, null, null); // Ajustar listeners si es necesario
        binding.recyclerViewReservas.setAdapter(reservasAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
