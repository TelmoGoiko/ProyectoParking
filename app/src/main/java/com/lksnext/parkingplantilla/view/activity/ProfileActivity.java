package com.lksnext.parkingplantilla.view.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lksnext.parkingplantilla.databinding.ActivityProfileBinding;
import com.lksnext.parkingplantilla.databinding.ActivityReservasBinding;
import com.lksnext.parkingplantilla.viewmodel.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Asignamos la vista/interfaz de perfil
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
