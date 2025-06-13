package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.FragmentMainMenuBinding;

public class MainMenuFragment extends Fragment {
    private FragmentMainMenuBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Configurar navegación para los botones grandes
        binding.btnUsuario.setOnClickListener(v -> navigate(R.id.userFragment));
        binding.btnReservar.setOnClickListener(v -> navigate(R.id.elegirReservaFragment));
        binding.btnMisReservas.setOnClickListener(v -> navigate(R.id.misReservasFragment));
        binding.btnMisVehiculos.setOnClickListener(v -> navigate(R.id.misVehiculosFragment));
    }

    private void navigate(int destinationId) {
        Navigation.findNavController(requireView()).navigate(destinationId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
