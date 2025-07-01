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
import com.lksnext.parkingplantilla.databinding.FragmentReservarBinding;
import com.lksnext.parkingplantilla.model.Vehicle;

import java.util.Calendar;

public class ReservarFragment extends Fragment {
    private FragmentReservarBinding binding;
    private Vehicle selectedVehicle;

    private Calendar fechaEntrada = Calendar.getInstance();
    private Calendar horaEntrada = Calendar.getInstance();
    private Calendar fechaSalida = Calendar.getInstance();
    private Calendar horaSalida = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReservarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Use the shared Toolbar from MainActivity
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Reservar");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        requireActivity().findViewById(com.lksnext.parkingplantilla.R.id.mainToolbar).setOnClickListener(v -> requireActivity().onBackPressed());
        if (getArguments() != null && getArguments().containsKey("selectedVehicle")) {
            selectedVehicle = (Vehicle) getArguments().getSerializable("selectedVehicle");
        }
        binding.btnContinuarReserva.setOnClickListener(v -> {
            if (selectedVehicle == null) {
                Toast.makeText(getContext(), "No se ha recibido vehículo seleccionado", Toast.LENGTH_SHORT).show();
                return;
            }
            int dayEntrada = binding.datePickerEntrada.getDayOfMonth();
            int monthEntrada = binding.datePickerEntrada.getMonth() + 1;
            int yearEntrada = binding.datePickerEntrada.getYear();
            int hourEntrada = binding.timePickerEntrada.getHour();
            int minuteEntrada = binding.timePickerEntrada.getMinute();
            int daySalida = binding.datePickerSalida.getDayOfMonth();
            int monthSalida = binding.datePickerSalida.getMonth() + 1;
            int yearSalida = binding.datePickerSalida.getYear();
            int hourSalida = binding.timePickerSalida.getHour();
            int minuteSalida = binding.timePickerSalida.getMinute();
            String fechaEntrada = String.format("%02d/%02d/%04d", dayEntrada, monthEntrada, yearEntrada);
            String horaEntrada = String.format("%02d:%02d", hourEntrada, minuteEntrada);
            String fechaSalida = String.format("%02d/%02d/%04d", daySalida, monthSalida, yearSalida);
            String horaSalida = String.format("%02d:%02d", hourSalida, minuteSalida);
            Toast.makeText(getContext(), "Reservando: " + fechaEntrada + " " + horaEntrada + " - " + fechaSalida + " " + horaSalida + " con " + selectedVehicle.getName() + " (" + selectedVehicle.getLicensePlate() + ")", Toast.LENGTH_SHORT).show();
            // Aquí puedes usar selectedVehicle, fecha/hora entrada y salida para la lógica de reserva

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
