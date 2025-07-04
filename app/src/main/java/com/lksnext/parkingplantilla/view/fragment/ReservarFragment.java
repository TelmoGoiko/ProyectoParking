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

import com.lksnext.parkingplantilla.databinding.FragmentReservarBinding;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.model.Vehicle;
import com.lksnext.parkingplantilla.viewmodel.ReservasViewModel;

import java.util.Calendar;

public class ReservarFragment extends Fragment {
    private FragmentReservarBinding binding;
    private Vehicle selectedVehicle;
    private ReservasViewModel reservasViewModel;
    private String reservaIdEdit = null;
    private boolean isEditMode = false;

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
        requireActivity().findViewById(com.lksnext.parkingplantilla.R.id.mainToolbar).setOnClickListener(v ->
            requireActivity().getOnBackPressedDispatcher().onBackPressed());

        // Inicializar ViewModel
        reservasViewModel = new ViewModelProvider(requireActivity()).get(ReservasViewModel.class);

        // Detectar si es edición
        if (getArguments() != null && getArguments().containsKey("reservaId")) {
            reservaIdEdit = getArguments().getString("reservaId");
            isEditMode = true;
        }

        // Mostrar aviso y cambiar texto del botón si es edición
        if (isEditMode) {
            binding.tvEditandoReserva.setVisibility(View.VISIBLE);
            binding.btnContinuarReserva.setText("Confirmar edición");
            // Cargar datos de la reserva
            reservasViewModel.getReservaById(reservaIdEdit);
            reservasViewModel.getSelectedReserva().observe(getViewLifecycleOwner(), reserva -> {
                if (reserva != null) {
                    // Precargar datos en los campos
                    String[] fechaParts = reserva.getFecha().split("/");
                    if (fechaParts.length == 3) {
                        binding.datePickerEntrada.updateDate(
                            Integer.parseInt(fechaParts[2]),
                            Integer.parseInt(fechaParts[1]) - 1,
                            Integer.parseInt(fechaParts[0])
                        );
                        binding.datePickerSalida.updateDate(
                            Integer.parseInt(fechaParts[2]),
                            Integer.parseInt(fechaParts[1]) - 1,
                            Integer.parseInt(fechaParts[0])
                        );
                    }
                    if (reserva.getHoraInicio() != null) {
                        java.util.Calendar calEntrada = java.util.Calendar.getInstance();
                        calEntrada.setTimeInMillis(reserva.getHoraInicio().getHoraInicio() * 1000);
                        binding.timePickerEntrada.setHour(calEntrada.get(java.util.Calendar.HOUR_OF_DAY));
                        binding.timePickerEntrada.setMinute(calEntrada.get(java.util.Calendar.MINUTE));
                        java.util.Calendar calSalida = java.util.Calendar.getInstance();
                        calSalida.setTimeInMillis(reserva.getHoraInicio().getHoraFin() * 1000);
                        binding.timePickerSalida.setHour(calSalida.get(java.util.Calendar.HOUR_OF_DAY));
                        binding.timePickerSalida.setMinute(calSalida.get(java.util.Calendar.MINUTE));
                    }
                }
            });
        } else {
            binding.tvEditandoReserva.setVisibility(View.GONE);
            binding.btnContinuarReserva.setText("Continuar");
        }

        // Obtener vehículo seleccionado de argumentos
        if (getArguments() != null && getArguments().containsKey("selectedVehicle")) {
            selectedVehicle = (Vehicle) getArguments().getSerializable("selectedVehicle");
        }

        // Configurar fecha y hora actuales
        Calendar calendar = Calendar.getInstance();
        binding.datePickerEntrada.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        binding.timePickerEntrada.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        binding.timePickerEntrada.setMinute(calendar.get(Calendar.MINUTE));

        // Para la fecha de salida, sumar 2 horas por defecto
        calendar.add(Calendar.HOUR_OF_DAY, 2);
        binding.datePickerSalida.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        binding.timePickerSalida.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        binding.timePickerSalida.setMinute(calendar.get(Calendar.MINUTE));

        binding.btnContinuarReserva.setOnClickListener(v -> {
            if (selectedVehicle == null && !isEditMode) {
                Toast.makeText(getContext(), "No se ha recibido vehículo seleccionado", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar fechas y horas
            Calendar fechaHoraEntrada = Calendar.getInstance();
            fechaHoraEntrada.set(
                    binding.datePickerEntrada.getYear(),
                    binding.datePickerEntrada.getMonth(),
                    binding.datePickerEntrada.getDayOfMonth(),
                    binding.timePickerEntrada.getHour(),
                    binding.timePickerEntrada.getMinute()
            );

            Calendar fechaHoraSalida = Calendar.getInstance();
            fechaHoraSalida.set(
                    binding.datePickerSalida.getYear(),
                    binding.datePickerSalida.getMonth(),
                    binding.datePickerSalida.getDayOfMonth(),
                    binding.timePickerSalida.getHour(),
                    binding.timePickerSalida.getMinute()
            );

            // Verificar que la fecha/hora de entrada sea anterior a la de salida
            if (fechaHoraEntrada.after(fechaHoraSalida)) {
                Toast.makeText(getContext(), "La fecha y hora de entrada debe ser anterior a la de salida", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar que la fecha/hora de entrada sea posterior a la actual
            Calendar ahora = Calendar.getInstance();
            if (fechaHoraEntrada.before(ahora)) {
                Toast.makeText(getContext(), "La fecha y hora de entrada debe ser posterior a la actual", Toast.LENGTH_SHORT).show();
                return;
            }

            // Formatear fechas y horas
            int dayEntrada = binding.datePickerEntrada.getDayOfMonth();
            int monthEntrada = binding.datePickerEntrada.getMonth() + 1;
            int yearEntrada = binding.datePickerEntrada.getYear();
            String fechaFormatted = String.format("%02d/%02d/%04d", dayEntrada, monthEntrada, yearEntrada);
            long horaInicioTimestamp = fechaHoraEntrada.getTimeInMillis() / 1000;
            long horaFinTimestamp = fechaHoraSalida.getTimeInMillis() / 1000;
            Hora hora = new Hora(horaInicioTimestamp, horaFinTimestamp);

            if (isEditMode) {
                // Confirmar edición y guardar cambios
                reservasViewModel.getSelectedReserva().observe(getViewLifecycleOwner(), reserva -> {
                    if (reserva != null) {
                        reserva.setFecha(fechaFormatted);
                        reserva.setHoraInicio(hora);
                        reservasViewModel.updateReserva(reserva);
                        Toast.makeText(getContext(), "Reserva actualizada", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    }
                });
            } else {
                // Pasar datos al siguiente fragmento (ElegirReservaFragment)
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectedVehicle", selectedVehicle);
                bundle.putString("fecha", fechaFormatted);
                bundle.putLong("horaInicio", horaInicioTimestamp);
                bundle.putLong("horaFin", horaFinTimestamp);

                // Cargar plazas disponibles
                reservasViewModel.loadAvailablePlazas(fechaFormatted, hora);

                // Navegar al fragmento de selección de plaza
                Navigation.findNavController(requireView()).navigate(
                        com.lksnext.parkingplantilla.R.id.action_reservarFragment_to_seleccionarPlazaFragment,
                        bundle
                );
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
