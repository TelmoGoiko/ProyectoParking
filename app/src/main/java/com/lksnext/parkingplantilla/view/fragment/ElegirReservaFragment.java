package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.lksnext.parkingplantilla.databinding.FragmentElegirReservaBinding;
import com.lksnext.parkingplantilla.R;

public class ElegirReservaFragment extends Fragment {
    private FragmentElegirReservaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentElegirReservaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        binding.btnReservar.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_elegirReservaFragment_to_reservarFragment));
        binding.btnAparcarYa.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_elegirReservaFragment_to_aparcarYaFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

