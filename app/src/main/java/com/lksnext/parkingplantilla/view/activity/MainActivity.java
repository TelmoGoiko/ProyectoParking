package com.lksnext.parkingplantilla.view.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Solicitar permiso de notificaciones en Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(updateLocale(newBase));
    }

    private static Context updateLocale(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String langCode = prefs.getString("app_language", Locale.getDefault().getLanguage());
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
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
