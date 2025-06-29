package com.lksnext.parkingplantilla.view.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up the shared Toolbar as the ActionBar
        setSupportActionBar(binding.mainToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.flFragment);
        // Obtener la referencia correcta al BottomNavigationView a través del binding
        BottomNavigationView bottomNavigationView = binding.bottomNavInclude.bottomNavigationView;

        if (navHostFragment != null && bottomNavigationView != null) {
            navController = navHostFragment.getNavController();

            // Reemplazando la configuración automática con un listener personalizado
            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();

                    // Manejar la navegación igual que en MainMenuFragment
                    if (itemId == R.id.mainMenuFragment) {
                        navController.navigate(R.id.mainMenuFragment);
                        return true;
                    } else if (itemId == R.id.userFragment) {
                        navController.navigate(R.id.userFragment);
                        return true;
                    } else if (itemId == R.id.reservarFragment) {
                        // Navegar a elegirReservaFragment en lugar de reservarFragment directamente
                        navController.navigate(R.id.elegirReservaFragment);
                        return true;
                    } else if (itemId == R.id.misReservasFragment) {
                        navController.navigate(R.id.misReservasFragment);
                        return true;
                    } else if (itemId == R.id.misVehiculosFragment) {
                        navController.navigate(R.id.misVehiculosFragment);
                        return true;
                    }
                    return false;
                }
            });

            // También añadimos un selector para mantener resaltado el elemento seleccionado
            bottomNavigationView.setOnItemReselectedListener(item -> {
                // No hacer nada para evitar recrear el fragmento al pulsar el mismo botón
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }
}
