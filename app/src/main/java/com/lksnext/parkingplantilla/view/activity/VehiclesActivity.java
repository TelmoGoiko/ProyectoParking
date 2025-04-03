package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityVehiclesBinding;

public class VehiclesActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ActivityVehiclesBinding binding;
    NavController navController;
    AppBarConfiguration appBarConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //...
        //Con el NavigationHost podremos movernos por distintas pestañas dentro de la misma pantalla
        NavHostFragment navHostFragment =
            (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.flFragment);
        navController = navHostFragment.getNavController();
        //...
        //Dependendiendo que boton clique el usuario de la navegacion se hacen distintas cosas
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.newres) {
                navController.navigate(R.id.mainFragment);
                return true;
            } else if (itemId == R.id.reservations) {
                navController.navigate(R.id.reservations);
                return true;
            } else if (itemId == R.id.person) {
                navController.navigate(R.id.person);
                return true;
            } else if (itemId == R.id.vehicles) {
                navController.navigate(R.id.vehicles);
                return true;
            }
            return false;
        });
    }
    //...
}
