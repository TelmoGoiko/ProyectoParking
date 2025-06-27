package com.lksnext.parkingplantilla.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.databinding.ActivityRegisterBinding;
import com.lksnext.parkingplantilla.viewmodel.RegisterViewModel;
import com.google.firebase.auth.FirebaseAuth;

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

        // Observa el resultado del registro
        registerViewModel.isRegistered().observe(this, isRegistered -> {
            if (isRegistered == null) return;
            if (isRegistered) {
                android.widget.Toast.makeText(this, "Registro completado. Ahora puedes iniciar sesión.", android.widget.Toast.LENGTH_LONG).show();
                finish(); // Cierra la pantalla y vuelve al login
            }
        });
        registerViewModel.getErrorMessage().observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (errorMsg.toLowerCase().contains("usuario")) {
                    binding.usernameLayout.setError(errorMsg);
                } else if (errorMsg.toLowerCase().contains("mail") || errorMsg.toLowerCase().contains("correo")) {
                    binding.emailLayout.setError(errorMsg);
                } else {
                    android.widget.Toast.makeText(this, errorMsg, android.widget.Toast.LENGTH_LONG).show();
                }
            } else {
                binding.usernameLayout.setError(null);
                binding.emailLayout.setError(null);
            }
        });

        // Acción para el botón de registro
        binding.btnRegister.setOnClickListener(v -> {
            String email = binding.emailText.getText().toString().trim();
            String password = binding.passwordText.getText().toString();
            String checkPassword = binding.checkPasswordText.getText().toString();
            String username = binding.editTextUsername.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || checkPassword.isEmpty() || username.isEmpty()) {
                binding.emailLayout.setError(email.isEmpty() ? "Campo requerido" : null);
                binding.passwordLayout.setError(password.isEmpty() ? "Campo requerido" : null);
                binding.checkPasswordLayout.setError(checkPassword.isEmpty() ? "Campo requerido" : null);
                binding.usernameLayout.setError(username.isEmpty() ? "Campo requerido" : null);
                return;
            }
            binding.emailLayout.setError(null);
            binding.passwordLayout.setError(null);
            binding.checkPasswordLayout.setError(null);
            binding.usernameLayout.setError(null);

            if (!password.equals(checkPassword)) {
                binding.checkPasswordLayout.setError("Las contraseñas no coinciden");
                return;
            }
            binding.checkPasswordLayout.setError(null);

            // Registro usando el ViewModel
            registerViewModel.registerUser(username, email, password, checkPassword);
        });

        // Acción para volver al login con el enlace
        binding.backToLogin.setOnClickListener(v -> {
            finish();
        });
    }
}