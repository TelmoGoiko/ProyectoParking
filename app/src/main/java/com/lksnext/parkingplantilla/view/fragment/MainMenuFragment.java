package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.FragmentMainMenuBinding;
import com.lksnext.parkingplantilla.view.activity.MainActivity;

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
        // Use the shared Toolbar from MainActivity
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Inicio");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false); // Oculta la flecha de atrás
        }
        requireActivity().findViewById(R.id.mainToolbar).setOnClickListener(v -> requireActivity().onBackPressed());

        binding.btnUsuario.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToMenuItem(R.id.userFragment);
                ((MainActivity) getActivity()).binding.bottomNavInclude.bottomNavigationView.setSelectedItemId(R.id.userFragment);
            }
        });
        binding.btnReservar.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToMenuItem(R.id.reservarFragment);
                ((MainActivity) getActivity()).binding.bottomNavInclude.bottomNavigationView.setSelectedItemId(R.id.reservarFragment);
            }
        });
        binding.btnMisReservas.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToMenuItem(R.id.misReservasFragment);
                ((MainActivity) getActivity()).binding.bottomNavInclude.bottomNavigationView.setSelectedItemId(R.id.misReservasFragment);
            }
        });
        binding.btnMisVehiculos.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToMenuItem(R.id.misVehiculosFragment);
                ((MainActivity) getActivity()).binding.bottomNavInclude.bottomNavigationView.setSelectedItemId(R.id.misVehiculosFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
