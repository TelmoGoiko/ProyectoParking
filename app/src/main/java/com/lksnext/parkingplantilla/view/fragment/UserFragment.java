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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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
        // Configurar Toolbar con botón de retroceso
        Toolbar toolbar = binding.toolbarUser;
        if (toolbar != null) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        }

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.loadUserData();

        // Mostrar email
        profileViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
            if (email != null) binding.textViewUserEmail.setText(email);
        });

        // Mostrar nombre de usuario desde Firestore
        FirebaseFirestore.getInstance().collection("users")
            .document(profileViewModel.getCurrentUserUid())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    if (username != null) binding.textViewUserName.setText(username);
                }
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
            FirebaseAuth.getInstance().signOut();
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
