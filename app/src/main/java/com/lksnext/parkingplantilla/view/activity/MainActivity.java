package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityMainBinding;
import com.lksnext.parkingplantilla.databinding.BottomNavigationBarBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflar la vista usando View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar botones grandes
        setupButtons();

        try {
            // Configurar la barra de navegación inferior
            // Accedemos directamente a la barra de navegación a través del binding incluido
            BottomNavigationView bottomNavigationView = binding.bottomNavInclude.bottomNavigationView;

            // Obtener el controlador de navegación de forma segura
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.flFragment);

            if (navHostFragment != null) {
                navController = navHostFragment.getNavController();
                // Conectar la barra de navegación con el controlador de navegación
                NavigationUI.setupWithNavController(bottomNavigationView, navController);

                // Configurar comportamiento de selección de elementos en la barra de navegación
                bottomNavigationView.setOnItemSelectedListener(item -> {
                    int itemId = item.getItemId();

                    if (itemId == R.id.profile) {
                        try {
                            // Navegar directamente a UserActivity en lugar de simular el clic
                            Intent intent = new Intent(MainActivity.this, UserActivity.class);
                            startActivity(intent);
                            return true;
                        } catch (Exception e) {
                            // Mostrar el error para diagnóstico
                            String errorMessage = "Error al abrir pantalla de usuario desde menú: " + e.getMessage();
                            android.util.Log.e("MainActivity", errorMessage, e);
                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            return false;
                        }
                    } else if (itemId == R.id.parkNow) {
                        // Simular clic en el botón de Aparcar Ya
                        binding.btnAparcarYa.performClick();
                        return true;
                    } else if (itemId == R.id.reserve) {
                        // Simular clic en el botón de Reservar
                        binding.btnReservar.performClick();
                        return true;
                    } else if (itemId == R.id.reservations) {
                        // Simular clic en el botón de Mis Reservas
                        binding.btnMisReservas.performClick();
                        return true;
                    } else if (itemId == R.id.vehicles) {
                        // Simular clic en el botón de Mis Vehículos
                        binding.btnMisVehiculos.performClick();
                        return true;
                    }

                    return false;
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupButtons() {
        // Configurar el botón "Mi Perfil"
        binding.btnUsuario.setOnClickListener(v -> {
            try {
                // Navegar a la pantalla de perfil de usuario
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                // Mostrar el error para diagnóstico
                String errorMessage = "Error al abrir pantalla de usuario: " + e.getMessage();
                android.util.Log.e("MainActivity", errorMessage, e);
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        // Configurar el botón "Aparcar Ya"
        binding.btnAparcarYa.setOnClickListener(v -> {
            // TODO: Implementar navegación a la pantalla de "Aparcar Ya"
            showToast("Aparcar Ya");
        });

        // Configurar el botón "Reservar"
        binding.btnReservar.setOnClickListener(v -> {
            // TODO: Implementar navegación a la pantalla de reserva
            showToast("Reservar");
        });

        // Configurar el botón "Mis Reservas"
        binding.btnMisReservas.setOnClickListener(v -> {
            // TODO: Implementar navegación a la pantalla de mis reservas
            showToast("Mis Reservas");
        });

        // Configurar el botón "Mis Vehículos"
        binding.btnMisVehiculos.setOnClickListener(v -> {
            // TODO: Implementar navegación a la pantalla de mis vehículos
            showToast("Mis Vehículos");
        });
    }

    private void showToast(String message) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }
}
