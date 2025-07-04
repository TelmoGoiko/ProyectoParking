package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.lksnext.parkingplantilla.databinding.FragmentAparcarYaBinding;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.model.Vehicle;
import com.lksnext.parkingplantilla.viewmodel.ReservasViewModel;
import com.lksnext.parkingplantilla.R;

import java.util.Calendar;

public class AparcarYaFragment extends Fragment {
    private FragmentAparcarYaBinding binding;
    private Vehicle selectedVehicle;
    private ReservasViewModel reservasViewModel;

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
        requireActivity().findViewById(R.id.mainToolbar).setOnClickListener(v ->
            requireActivity().getOnBackPressedDispatcher().onBackPressed());

        // Inicializar ViewModel
        reservasViewModel = new ViewModelProvider(requireActivity()).get(ReservasViewModel.class);

        if (getArguments() != null && getArguments().containsKey("selectedVehicle")) {
            selectedVehicle = (Vehicle) getArguments().getSerializable("selectedVehicle");
        }

        // Establecer fecha de salida con hora actual + 2 horas por defecto
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        binding.datePickerSalida.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        binding.timePickerSalida.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        binding.timePickerSalida.setMinute(calendar.get(Calendar.MINUTE));

        binding.btnContinuarAparcarYa.setOnClickListener(v -> {
            if (selectedVehicle == null) {
                Toast.makeText(getContext(), "No se ha recibido vehículo seleccionado", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener la fecha y hora actual para la entrada
            Calendar fechaHoraEntrada = Calendar.getInstance();

            // Obtener la fecha y hora de salida
            Calendar fechaHoraSalida = Calendar.getInstance();
            fechaHoraSalida.set(
                    binding.datePickerSalida.getYear(),
                    binding.datePickerSalida.getMonth(),
                    binding.datePickerSalida.getDayOfMonth(),
                    binding.timePickerSalida.getHour(),
                    binding.timePickerSalida.getMinute()
            );

            // Verificar que la fecha/hora de salida sea posterior a la actual
            if (fechaHoraSalida.before(fechaHoraEntrada) || fechaHoraSalida.equals(fechaHoraEntrada)) {
                Toast.makeText(getContext(), "La fecha y hora de salida debe ser posterior a la actual", Toast.LENGTH_SHORT).show();
                return;
            }

            // Formatear la fecha actual para la reserva
            int day = fechaHoraEntrada.get(Calendar.DAY_OF_MONTH);
            int month = fechaHoraEntrada.get(Calendar.MONTH) + 1;
            int year = fechaHoraEntrada.get(Calendar.YEAR);
            String fechaFormatted = String.format("%02d/%02d/%04d", day, month, year);

            // Convertir a timestamp (segundos desde epoch) para la clase Hora
            long horaInicioTimestamp = fechaHoraEntrada.getTimeInMillis() / 1000;
            long horaFinTimestamp = fechaHoraSalida.getTimeInMillis() / 1000;

            // Crear objeto Hora
            Hora hora = new Hora(horaInicioTimestamp, horaFinTimestamp);

            // Pasar datos al siguiente fragmento (SeleccionarPlazaFragment)
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedVehicle", selectedVehicle);
            bundle.putString("fecha", fechaFormatted);
            bundle.putLong("horaInicio", horaInicioTimestamp);
            bundle.putLong("horaFin", horaFinTimestamp);
            bundle.putBoolean("aparcarYa", true); // Flag para indicar que viene de AparcarYa

            // Cargar plazas disponibles
            reservasViewModel.loadAvailablePlazas(fechaFormatted, hora);

            // Navegar al fragmento de selección de plaza
            Navigation.findNavController(requireView()).navigate(
                    R.id.action_aparcarYaFragment_to_seleccionarPlazaFragment,
                    bundle
            );
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
