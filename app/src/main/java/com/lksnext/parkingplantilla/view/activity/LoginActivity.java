package com.lksnext.parkingplantilla.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parkingplantilla.databinding.ActivityLoginBinding;
import com.lksnext.parkingplantilla.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Asignamos la vista/interfaz login (layout)
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.isLogged().observe(this, isLogged -> {
            if (isLogged == null) return;
            if (isLogged) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                binding.usernameLayout.setError("Error en el login: credenciales incorrectas o usuario no existe");
            }
        });

        //Acciones a realizar cuando el usuario clica el boton de login
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.username.getText().toString();
            String password = binding.password.getText().toString();
            binding.usernameLayout.setError(null);
            binding.passwordLayout.setError(null);
            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) binding.usernameLayout.setError("Campo requerido");
                if (password.isEmpty()) binding.passwordLayout.setError("Campo requerido");
                return;
            }
            loginViewModel.loginUser(email, password);
        });

        //Acciones a realizar cuando el usuario clica el boton de crear cuenta (se cambia de pantalla)
        binding.createAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        //Acción para recuperar contraseña
        binding.forgotPassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Recuperar contraseña");
            final EditText input = new EditText(this);
            input.setHint("Introduce tu email");
            builder.setView(input);
            builder.setPositiveButton("Enviar", (dialog, which) -> {
                String email = input.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(this, "Introduce un email válido", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Error: " + (task.getException() != null ? task.getException().getMessage() : ""), Toast.LENGTH_LONG).show();
                        }
                    });
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
            builder.show();
        });
    }
}