package com.lksnext.parkingplantilla.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.databinding.ActivityLoginBinding;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel loginViewModel;
    private DataRepository repository;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Asignamos la vista/interfaz login (layout)
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = DataRepository.getInstance();
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

                repository.sendPasswordResetEmail(email, new Callback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(LoginActivity.this, "Correo de recuperación enviado", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure() {
                        Toast.makeText(LoginActivity.this, "Error al enviar el correo de recuperación", Toast.LENGTH_LONG).show();
                    }
                });
            });
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.lksnext.parkingplantilla.R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.googleButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleGoogleSignIn(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Error en Google Sign-In: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void handleGoogleSignIn(GoogleSignInAccount account) {
        repository.firebaseAuthWithGoogle(account, new Callback() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure() {
                Toast.makeText(LoginActivity.this, "Error autenticando con Google", Toast.LENGTH_LONG).show();
            }
        });
    }
}