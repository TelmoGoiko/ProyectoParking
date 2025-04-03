package com.lksnext.parkingplantilla.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.databinding.ActivityReservasBinding;
import com.lksnext.parkingplantilla.viewmodel.ReservasViewModel;

public class ReservasActivity extends AppCompatActivity {

    private ActivityReservasBinding binding;
    private ReservasViewModel reservasViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asignamos la vista/interfaz de reservas
        binding = ActivityReservasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Asignamos el viewModel de reservas
        reservasViewModel = new ViewModelProvider(this).get(ReservasViewModel.class);
    }
}
