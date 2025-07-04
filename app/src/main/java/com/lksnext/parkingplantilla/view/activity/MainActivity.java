package com.lksnext.parkingplantilla.view.activity;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;
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

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Reemplazando la configuración automática con un listener personalizado
            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    return navigateToMenuItem(item.getItemId());
                }
            });

            // También añadimos un selector para mantener resaltado el elemento seleccionado
            bottomNavigationView.setOnItemReselectedListener(item -> {
                // No hacer nada para evitar recrear el fragmento al pulsar el mismo botón
            });
        }
    }

    /**
     * Permite navegar a un destino según el itemId, igual que la barra de navegación.
     */
    public boolean navigateToMenuItem(int itemId) {
        if (navController == null) return false;
        // Comprobar si ya estamos en el destino
        int currentDest = navController.getCurrentDestination() != null ? navController.getCurrentDestination().getId() : -1;
        int navTargetId = itemId;
        if (itemId == R.id.reservarFragment) {
            navTargetId = R.id.elegirReservaFragment;
        }
        if (currentDest == navTargetId) return true;
        // Navegar solo si no estamos ya en el destino
        navController.navigate(navTargetId);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }
}
