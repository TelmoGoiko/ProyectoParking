package com.lksnext.parkingplantilla.view.fragment;

import android.content.Intent;
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
import com.lksnext.parkingplantilla.databinding.FragmentUserBinding;
import com.lksnext.parkingplantilla.view.activity.LoginActivity;
import com.lksnext.parkingplantilla.viewmodel.ProfileViewModel;

public class UserFragment extends Fragment {
    private FragmentUserBinding binding;
    private ProfileViewModel profileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Use the shared Toolbar from MainActivity
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Perfil de Usuario");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        requireActivity().findViewById(com.lksnext.parkingplantilla.R.id.mainToolbar).setOnClickListener(v ->
            requireActivity().getOnBackPressedDispatcher().onBackPressed());

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.loadUserData();

        // Mostrar email
        profileViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
            if (email != null) binding.textViewUserEmail.setText(email);
        });

        // Mostrar nombre de usuario
        profileViewModel.getUserName().observe(getViewLifecycleOwner(), username -> {
            if (username != null) binding.textViewUserName.setText(username);
        });

        // Botón cambiar contraseña
        binding.btnChangePassword.setOnClickListener(v -> {
            String email = binding.textViewUserEmail.getText().toString();
            profileViewModel.sendPasswordResetEmail(email);
        });

        profileViewModel.getPasswordResetResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
        });

        // Botón de logout
        binding.btnLogout.setOnClickListener(v -> {
            profileViewModel.signOut();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
