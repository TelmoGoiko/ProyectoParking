package com.lksnext.parkingplantilla.view.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.lksnext.parkingplantilla.databinding.FragmentAparcarYaBinding;
import java.util.Calendar;

public class AparcarYaFragment extends Fragment {
    private FragmentAparcarYaBinding binding;
    private Calendar fechaSeleccionada = Calendar.getInstance();
    private Calendar horaSeleccionada = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAparcarYaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        actualizarTextoFecha();
        actualizarTextoHora();
        binding.btnSeleccionarFecha.setOnClickListener(v -> mostrarDatePicker());
        binding.btnSeleccionarHora.setOnClickListener(v -> mostrarTimePicker());
        binding.btnAparcar.setOnClickListener(v -> {
            String fecha = binding.btnSeleccionarFecha.getText().toString();
            String hora = binding.btnSeleccionarHora.getText().toString();
            Toast.makeText(getContext(), "Aparcando el " + fecha + " a las " + hora, Toast.LENGTH_SHORT).show();
        });
    }

    private void mostrarDatePicker() {
        Calendar hoy = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            fechaSeleccionada.set(Calendar.YEAR, year);
            fechaSeleccionada.set(Calendar.MONTH, month);
            fechaSeleccionada.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            actualizarTextoFecha();
        },
        fechaSeleccionada.get(Calendar.YEAR),
        fechaSeleccionada.get(Calendar.MONTH),
        fechaSeleccionada.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(hoy.getTimeInMillis());
        dialog.show();
    }

    private void mostrarTimePicker() {
        TimePickerDialog dialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            horaSeleccionada.set(Calendar.HOUR_OF_DAY, hourOfDay);
            horaSeleccionada.set(Calendar.MINUTE, minute);
            actualizarTextoHora();
        },
        horaSeleccionada.get(Calendar.HOUR_OF_DAY),
        horaSeleccionada.get(Calendar.MINUTE), true);
        dialog.show();
    }

    private void actualizarTextoFecha() {
        int year = fechaSeleccionada.get(Calendar.YEAR);
        int month = fechaSeleccionada.get(Calendar.MONTH) + 1;
        int day = fechaSeleccionada.get(Calendar.DAY_OF_MONTH);
        binding.btnSeleccionarFecha.setText(String.format("%02d/%02d/%04d", day, month, year));
    }

    private void actualizarTextoHora() {
        int hour = horaSeleccionada.get(Calendar.HOUR_OF_DAY);
        int minute = horaSeleccionada.get(Calendar.MINUTE);
        binding.btnSeleccionarHora.setText(String.format("%02d:%02d", hour, minute));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
