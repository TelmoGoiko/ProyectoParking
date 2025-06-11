package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lksnext.parkingplantilla.R;

public class UserActivity extends AppCompatActivity {

    // Vistas de la interfaz de usuario
    private ImageButton btnBack;
    private TextView tvUserName, tvUserEmail;
    private EditText etName, etEmail, etPhone;
    private Button btnSave, btnLogout;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Inicializar vistas
        initViews();

        // Configurar listeners
        setupListeners();

        // Cargar datos del usuario (simulado)
        loadUserData();

        // Configurar la barra de navegación inferior
        setupBottomNavigation();
    }

    private void initViews() {
        try {
            btnBack = findViewById(R.id.btnBack);
            tvUserName = findViewById(R.id.tvUserName);
            tvUserEmail = findViewById(R.id.tvUserEmail);
            etName = findViewById(R.id.etName);
            etEmail = findViewById(R.id.etEmail);
            etPhone = findViewById(R.id.etPhone);
            btnSave = findViewById(R.id.btnSave);
            btnLogout = findViewById(R.id.btnLogout);
            bottomNavigationView = findViewById(R.id.bottomNavigationView);
        } catch (Exception e) {
            // Capturar y registrar cualquier error durante la inicialización de vistas
            String errorMessage = "Error al inicializar vistas: " + e.getMessage();
            android.util.Log.e("UserActivity", errorMessage, e);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void setupListeners() {
        try {
            // Botón de volver atrás
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            // Botón de guardar
            if (btnSave != null) {
                btnSave.setOnClickListener(v -> {
                    saveUserData();
                    Toast.makeText(UserActivity.this, "Información guardada", Toast.LENGTH_SHORT).show();
                });
            }

            // Botón de cerrar sesión
            if (btnLogout != null) {
                btnLogout.setOnClickListener(v -> {
                    // Simulación de cierre de sesión
                    Toast.makeText(UserActivity.this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();

                    // Volver a la pantalla de login
                    Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        } catch (Exception e) {
            String errorMessage = "Error al configurar listeners: " + e.getMessage();
            android.util.Log.e("UserActivity", errorMessage, e);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void loadUserData() {
        try {
            // Simulación de carga de datos del usuario
            // En una implementación real, estos datos vendrían de una base de datos o API
            String userName = "Carlos García";
            String userEmail = "carlos.garcia@example.com";
            String userPhone = "666123456";

            // Mostrar los datos en las vistas
            if (tvUserName != null) tvUserName.setText(userName);
            if (tvUserEmail != null) tvUserEmail.setText(userEmail);
            if (etName != null) etName.setText(userName);
            if (etEmail != null) etEmail.setText(userEmail);
            if (etPhone != null) etPhone.setText(userPhone);
        } catch (Exception e) {
            String errorMessage = "Error al cargar datos de usuario: " + e.getMessage();
            android.util.Log.e("UserActivity", errorMessage, e);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void saveUserData() {
        try {
            // Simulación de guardado de datos del usuario
            // En una implementación real, estos datos se guardarían en una base de datos o API
            String newName = etName != null ? etName.getText().toString() : "";
            String newEmail = etEmail != null ? etEmail.getText().toString() : "";
            String newPhone = etPhone != null ? etPhone.getText().toString() : "";

            // Actualizar los datos mostrados
            if (tvUserName != null) tvUserName.setText(newName);
            if (tvUserEmail != null) tvUserEmail.setText(newEmail);
        } catch (Exception e) {
            String errorMessage = "Error al guardar datos de usuario: " + e.getMessage();
            android.util.Log.e("UserActivity", errorMessage, e);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void setupBottomNavigation() {
        try {
            // Configurar la navegación inferior si existe
            if (bottomNavigationView != null) {
                bottomNavigationView.setOnItemSelectedListener(item -> {
                    int itemId = item.getItemId();

                    if (itemId == R.id.profile) {
                        // Ya estamos en el perfil, no hacemos nada
                        return true;
                    } else if (itemId == R.id.parkNow) {
                        // Navegar a la pantalla de Aparcar Ya
                        navigateToActivity(MainActivity.class);
                        return true;
                    } else if (itemId == R.id.reserve) {
                        // Navegar a la pantalla de Reservar
                        navigateToActivity(MainActivity.class);
                        return true;
                    } else if (itemId == R.id.reservations) {
                        // Navegar a la pantalla de Mis Reservas
                        navigateToActivity(MainActivity.class);
                        return true;
                    } else if (itemId == R.id.vehicles) {
                        // Navegar a la pantalla de Mis Vehículos
                        navigateToActivity(MainActivity.class);
                        return true;
                    }

                    return false;
                });

                // Seleccionar el ítem de perfil
                bottomNavigationView.setSelectedItemId(R.id.profile);
            } else {
                android.util.Log.w("UserActivity", "No se pudo configurar la barra de navegación: bottomNavigationView es null");
            }
        } catch (Exception e) {
            String errorMessage = "Error al configurar navegación: " + e.getMessage();
            android.util.Log.e("UserActivity", errorMessage, e);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
