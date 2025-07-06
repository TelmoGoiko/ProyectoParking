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
            activity.getSupportActionBar().setTitle(getString(R.string.aparcar_ya));
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

        // Limitar el rango de fechas y horas de salida (máximo 8 horas desde ahora y máximo 7 días desde hoy)
        Calendar ahora = Calendar.getInstance();
        long hoyMillis = ahora.getTimeInMillis();
        long maxMillis = hoyMillis + 7L * 24 * 60 * 60 * 1000;
        binding.datePickerSalida.setMinDate(hoyMillis);
        binding.datePickerSalida.setMaxDate(maxMillis);
        binding.timePickerSalida.setIs24HourView(true);

        binding.timePickerSalida.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
            Calendar salida = Calendar.getInstance();
            salida.set(binding.datePickerSalida.getYear(), binding.datePickerSalida.getMonth(), binding.datePickerSalida.getDayOfMonth(), hourOfDay, minute);
            Calendar entrada = Calendar.getInstance(); // ahora
            // Si la salida es antes de la entrada, igualar
            if (salida.before(entrada)) {
                binding.datePickerSalida.updateDate(entrada.get(Calendar.YEAR), entrada.get(Calendar.MONTH), entrada.get(Calendar.DAY_OF_MONTH));
                binding.timePickerSalida.setHour(entrada.get(Calendar.HOUR_OF_DAY));
                binding.timePickerSalida.setMinute(entrada.get(Calendar.MINUTE));
            }
            // Si la diferencia es mayor a 8 horas, ajustar salida
            long diffMillis = salida.getTimeInMillis() - entrada.getTimeInMillis();
            if (diffMillis > 8 * 60 * 60 * 1000) {
                Calendar nuevaSalida = (Calendar) entrada.clone();
                nuevaSalida.add(Calendar.HOUR_OF_DAY, 8);
                binding.datePickerSalida.updateDate(nuevaSalida.get(Calendar.YEAR), nuevaSalida.get(Calendar.MONTH), nuevaSalida.get(Calendar.DAY_OF_MONTH));
                binding.timePickerSalida.setHour(nuevaSalida.get(Calendar.HOUR_OF_DAY));
                binding.timePickerSalida.setMinute(nuevaSalida.get(Calendar.MINUTE));
                Toast.makeText(getContext(), getString(R.string.estancia_no_supera_8h), Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnContinuarAparcarYa.setOnClickListener(v -> {
            if (selectedVehicle == null) {
                Toast.makeText(getContext(), getString(R.string.no_vehiculo_seleccionado), Toast.LENGTH_SHORT).show();
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

            // Validar que la fecha de salida esté entre ahora y 7 días
            Calendar hoy = Calendar.getInstance();
            hoy.set(Calendar.HOUR_OF_DAY, 0);
            hoy.set(Calendar.MINUTE, 0);
            hoy.set(Calendar.SECOND, 0);
            hoy.set(Calendar.MILLISECOND, 0);
            Calendar maxFecha = (Calendar) hoy.clone();
            maxFecha.add(Calendar.DAY_OF_YEAR, 7);
            Calendar fechaSeleccionada = Calendar.getInstance();
            fechaSeleccionada.set(binding.datePickerSalida.getYear(), binding.datePickerSalida.getMonth(), binding.datePickerSalida.getDayOfMonth(), 0, 0, 0);
            fechaSeleccionada.set(Calendar.MILLISECOND, 0);
            if (fechaSeleccionada.before(hoy) || fechaSeleccionada.after(maxFecha)) {
                Toast.makeText(getContext(), getString(R.string.solo_aparcar_7dias), Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar que la fecha/hora de salida sea posterior a la actual
            if (fechaHoraSalida.before(fechaHoraEntrada) || fechaHoraSalida.equals(fechaHoraEntrada)) {
                Toast.makeText(getContext(), getString(R.string.salida_posterior_actual), Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que la diferencia entre entrada y salida no supere 8 horas
            long diffMillisHoras = fechaHoraSalida.getTimeInMillis() - fechaHoraEntrada.getTimeInMillis();
            if (diffMillisHoras > 8 * 60 * 60 * 1000) {
                Toast.makeText(getContext(), getString(R.string.estancia_no_supera_8h), Toast.LENGTH_SHORT).show();
                return;
            }

            // Formatear la fecha actual para la reserva
            int day = fechaHoraEntrada.get(Calendar.DAY_OF_MONTH);
            int month = fechaHoraEntrada.get(Calendar.MONTH) + 1;
            int year = fechaHoraEntrada.get(Calendar.YEAR);
            String fechaFormatted = String.format("%02d/%02d/%04d", day, month, year);

            // Calcular segundos desde medianoche local para la fecha de entrada
            Calendar medianoche = (Calendar) fechaHoraEntrada.clone();
            medianoche.set(Calendar.HOUR_OF_DAY, 0);
            medianoche.set(Calendar.MINUTE, 0);
            medianoche.set(Calendar.SECOND, 0);
            medianoche.set(Calendar.MILLISECOND, 0);
            long horaInicio = (fechaHoraEntrada.getTimeInMillis() - medianoche.getTimeInMillis()) / 1000;
            long horaFin = (fechaHoraSalida.getTimeInMillis() - medianoche.getTimeInMillis()) / 1000;

            // Crear objeto Hora
            Hora hora = new Hora(horaInicio, horaFin);

            // Pasar datos al siguiente fragmento (SeleccionarPlazaFragment)
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedVehicle", selectedVehicle);
            bundle.putString("fecha", fechaFormatted);
            bundle.putLong("horaInicio", horaInicio);
            bundle.putLong("horaFin", horaFin);
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
