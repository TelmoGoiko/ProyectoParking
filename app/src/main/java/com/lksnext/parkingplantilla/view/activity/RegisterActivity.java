package com.lksnext.parkingplantilla.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.databinding.ActivityRegisterBinding;
import com.lksnext.parkingplantilla.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asignamos la vista/interfaz de registro
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Asignamos el viewModel de register
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Acción para el botón de registro
        binding.btnRegister.setOnClickListener(v -> {
            String email = binding.emailText.getText().toString().trim();
            String password = binding.passwordText.getText().toString();
            String checkPassword = binding.checkPasswordText.getText().toString();

            if (email.isEmpty() || password.isEmpty() || checkPassword.isEmpty()) {
                binding.emailLayout.setError(email.isEmpty() ? "Campo requerido" : null);
                binding.passwordLayout.setError(password.isEmpty() ? "Campo requerido" : null);
                binding.checkPasswordLayout.setError(checkPassword.isEmpty() ? "Campo requerido" : null);
                return;
            }
            binding.emailLayout.setError(null);
            binding.passwordLayout.setError(null);
            binding.checkPasswordLayout.setError(null);

            if (!password.equals(checkPassword)) {
                binding.checkPasswordLayout.setError("Las contraseñas no coinciden");
                return;
            }
            binding.checkPasswordLayout.setError(null);

            // Lógica de registro (simulada)
            registerViewModel.registerUser(email, password);
        });

        // Observa el resultado del registro
        registerViewModel.isRegistered().observe(this, registered -> {
            if (registered != null && registered) {
                finish(); // Vuelve al login tras registro exitoso
            }
        });

        // Acción para volver al login con el enlace
        binding.backToLogin.setOnClickListener(v -> {
            finish();
        });
    }
}