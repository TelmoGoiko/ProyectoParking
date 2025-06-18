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
import androidx.navigation.Navigation;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.FragmentReservarBinding;

import java.util.Calendar;

public class ReservarFragment extends Fragment {
    private FragmentReservarBinding binding;

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

        // Inicializar fechaSalida a un día después
        fechaSalida.add(Calendar.DAY_OF_MONTH, 1);

        // Configurar DatePicker de entrada
        DatePicker datePickerStart = binding.datePickerStart;
        datePickerStart.init(
            fechaEntrada.get(Calendar.YEAR),
            fechaEntrada.get(Calendar.MONTH),
            fechaEntrada.get(Calendar.DAY_OF_MONTH),
            (view1, year, monthOfYear, dayOfMonth) -> {
                fechaEntrada.set(Calendar.YEAR, year);
                fechaEntrada.set(Calendar.MONTH, monthOfYear);
                fechaEntrada.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Si la fecha de entrada es posterior a la de salida, actualizar la de salida
                if (fechaEntrada.after(fechaSalida)) {
                    Calendar nuevaFechaSalida = (Calendar) fechaEntrada.clone();
                    nuevaFechaSalida.add(Calendar.DAY_OF_MONTH, 1);
                    fechaSalida.set(Calendar.YEAR, nuevaFechaSalida.get(Calendar.YEAR));
                    fechaSalida.set(Calendar.MONTH, nuevaFechaSalida.get(Calendar.MONTH));
                    fechaSalida.set(Calendar.DAY_OF_MONTH, nuevaFechaSalida.get(Calendar.DAY_OF_MONTH));

                    // Actualizar el DatePicker de salida
                    binding.datePickerEnd.updateDate(
                        fechaSalida.get(Calendar.YEAR),
                        fechaSalida.get(Calendar.MONTH),
                        fechaSalida.get(Calendar.DAY_OF_MONTH)
                    );
                }
            }
        );

        // Configurar TimePicker de entrada
        TimePicker timePickerStart = binding.timePickerStart;
        timePickerStart.setIs24HourView(true);
        timePickerStart.setHour(horaEntrada.get(Calendar.HOUR_OF_DAY));
        timePickerStart.setMinute(horaEntrada.get(Calendar.MINUTE));
        timePickerStart.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
            horaEntrada.set(Calendar.HOUR_OF_DAY, hourOfDay);
            horaEntrada.set(Calendar.MINUTE, minute);
        });

        // Configurar DatePicker de salida
        DatePicker datePickerEnd = binding.datePickerEnd;
        datePickerEnd.init(
            fechaSalida.get(Calendar.YEAR),
            fechaSalida.get(Calendar.MONTH),
            fechaSalida.get(Calendar.DAY_OF_MONTH),
            (view1, year, monthOfYear, dayOfMonth) -> {
                fechaSalida.set(Calendar.YEAR, year);
                fechaSalida.set(Calendar.MONTH, monthOfYear);
                fechaSalida.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Si la fecha de salida es anterior a la de entrada, mostrar error
                if (fechaSalida.before(fechaEntrada)) {
                    Toast.makeText(getContext(), "La fecha de salida debe ser posterior a la de entrada", Toast.LENGTH_SHORT).show();

                    // Resetear a un día después de la entrada
                    Calendar nuevaFechaSalida = (Calendar) fechaEntrada.clone();
                    nuevaFechaSalida.add(Calendar.DAY_OF_MONTH, 1);
                    fechaSalida.set(Calendar.YEAR, nuevaFechaSalida.get(Calendar.YEAR));
                    fechaSalida.set(Calendar.MONTH, nuevaFechaSalida.get(Calendar.MONTH));
                    fechaSalida.set(Calendar.DAY_OF_MONTH, nuevaFechaSalida.get(Calendar.DAY_OF_MONTH));

                    // Actualizar el DatePicker
                    datePickerEnd.updateDate(
                        fechaSalida.get(Calendar.YEAR),
                        fechaSalida.get(Calendar.MONTH),
                        fechaSalida.get(Calendar.DAY_OF_MONTH)
                    );
                }
            }
        );

        // Configurar TimePicker de salida
        TimePicker timePickerEnd = binding.timePickerEnd;
        timePickerEnd.setIs24HourView(true);
        timePickerEnd.setHour(horaSalida.get(Calendar.HOUR_OF_DAY));
        timePickerEnd.setMinute(horaSalida.get(Calendar.MINUTE));
        timePickerEnd.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
            horaSalida.set(Calendar.HOUR_OF_DAY, hourOfDay);
            horaSalida.set(Calendar.MINUTE, minute);
        });

        // Configurar el botón de búsqueda
        binding.btnReservar.setOnClickListener(v -> {
            String fechaEntradaStr = String.format("%02d/%02d/%04d",
                fechaEntrada.get(Calendar.DAY_OF_MONTH),
                fechaEntrada.get(Calendar.MONTH) + 1,
                fechaEntrada.get(Calendar.YEAR));

            String horaEntradaStr = String.format("%02d:%02d",
                horaEntrada.get(Calendar.HOUR_OF_DAY),
                horaEntrada.get(Calendar.MINUTE));

            String fechaSalidaStr = String.format("%02d/%02d/%04d",
                fechaSalida.get(Calendar.DAY_OF_MONTH),
                fechaSalida.get(Calendar.MONTH) + 1,
                fechaSalida.get(Calendar.YEAR));

            String horaSalidaStr = String.format("%02d:%02d",
                horaSalida.get(Calendar.HOUR_OF_DAY),
                horaSalida.get(Calendar.MINUTE));

            String mensaje = String.format("Buscando parkings para reservar desde %s %s hasta %s %s",
                fechaEntradaStr, horaEntradaStr, fechaSalidaStr, horaSalidaStr);

            Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();

            // Navegar al fragmento de selección de vehículo
            Navigation.findNavController(view).navigate(R.id.action_reservarFragment_to_seleccionVehiculoFragment);
        });

        // Configurar el botón de navegación hacia atrás
        binding.toolbarReservar.setNavigationOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
