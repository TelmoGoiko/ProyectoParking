package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.FragmentAparcarYaBinding;
import com.lksnext.parkingplantilla.model.Vehicle;

public class AparcarYaFragment extends Fragment {
    private FragmentAparcarYaBinding binding;
    private Vehicle selectedVehicle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAparcarYaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Use the shared Toolbar from MainActivity
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Aparcar Ya");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        requireActivity().findViewById(com.lksnext.parkingplantilla.R.id.mainToolbar).setOnClickListener(v -> requireActivity().onBackPressed());
        if (getArguments() != null && getArguments().containsKey("selectedVehicle")) {
            selectedVehicle = (Vehicle) getArguments().getSerializable("selectedVehicle");
        }
        binding.btnContinuarAparcarYa.setOnClickListener(v -> {
            if (selectedVehicle == null) {
                Toast.makeText(getContext(), "No se ha recibido vehículo seleccionado", Toast.LENGTH_SHORT).show();
                return;
            }
            int day = binding.datePickerSalida.getDayOfMonth();
            int month = binding.datePickerSalida.getMonth() + 1;
            int year = binding.datePickerSalida.getYear();
            int hour = binding.timePickerSalida.getHour();
            int minute = binding.timePickerSalida.getMinute();
            String fecha = String.format("%02d/%02d/%04d", day, month, year);
            String hora = String.format("%02d:%02d", hour, minute);
            Toast.makeText(getContext(), "Aparcando el " + fecha + " a las " + hora + " con " + selectedVehicle.getName() + " (" + selectedVehicle.getLicensePlate() + ")", Toast.LENGTH_SHORT).show();
            // Aquí puedes usar selectedVehicle, fecha y hora para la lógica de aparcamiento
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
