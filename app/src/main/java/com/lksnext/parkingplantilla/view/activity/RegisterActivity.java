package com.lksnext.parkingplantilla.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.data.DataRepository;
import com.lksnext.parkingplantilla.databinding.ActivityRegisterBinding;
import com.lksnext.parkingplantilla.domain.Callback;
import com.lksnext.parkingplantilla.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private RegisterViewModel registerViewModel;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    private DataRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asignamos la vista/interfaz de registro
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Asignamos el viewModel de register
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Inicializar repository igual que en LoginActivity
        repository = DataRepository.getInstance();

        // Observa el resultado del registro
        registerViewModel.isRegistered().observe(this, isRegistered -> {
            if (isRegistered == null) return;
            if (isRegistered) {
                android.widget.Toast.makeText(this, getString(R.string.registro_completado), android.widget.Toast.LENGTH_LONG).show();
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
                binding.emailLayout.setError(email.isEmpty() ? getString(R.string.campo_requerido) : null);
                binding.passwordLayout.setError(password.isEmpty() ? getString(R.string.campo_requerido) : null);
                binding.checkPasswordLayout.setError(checkPassword.isEmpty() ? getString(R.string.campo_requerido) : null);
                binding.usernameLayout.setError(username.isEmpty() ? getString(R.string.campo_requerido) : null);
                return;
            }
            binding.emailLayout.setError(null);
            binding.passwordLayout.setError(null);
            binding.checkPasswordLayout.setError(null);
            binding.usernameLayout.setError(null);

            if (!password.equals(checkPassword)) {
                binding.checkPasswordLayout.setError(getString(R.string.contrasenas_no_coinciden));
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

        // Google Sign-In setup
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleGoogleSignIn(account);
            } catch (ApiException e) {
                Toast.makeText(this, getString(R.string.error_google_signin, e.getMessage()), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void handleGoogleSignIn(GoogleSignInAccount account) {
        repository.firebaseAuthWithGoogle(account, new Callback() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure() {
                Toast.makeText(RegisterActivity.this, getString(R.string.error_autenticando_google), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(updateLocale(newBase));
    }

    private static Context updateLocale(Context context) {
        android.content.SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String langCode = prefs.getString("app_language", java.util.Locale.getDefault().getLanguage());
        java.util.Locale locale;
        if (langCode.contains("-")) {
            String[] parts = langCode.split("-");
            locale = new java.util.Locale.Builder().setLanguage(parts[0]).setRegion(parts[1]).build();
        } else {
            locale = new java.util.Locale.Builder().setLanguage(langCode).build();
        }
        java.util.Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = res.getConfiguration();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
            return context;
        }
    }
}