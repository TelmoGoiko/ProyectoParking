package com.lksnext.parkingplantilla.view.fragment;

import android.app.DatePickerDialog;
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

        // Limitar el rango de fechas de los DatePickers (solo hoy hasta 7 días después)
        long hoyMillis = System.currentTimeMillis();
        long maxMillis = hoyMillis + 7L * 24 * 60 * 60 * 1000;
        binding.datePickerEntrada.setMinDate(hoyMillis);
        binding.datePickerEntrada.setMaxDate(maxMillis);
        binding.datePickerSalida.setMinDate(hoyMillis);
        binding.datePickerSalida.setMaxDate(maxMillis);

        // Listeners para limitar la diferencia máxima de 8 horas
        binding.timePickerEntrada.setIs24HourView(true);
        binding.timePickerSalida.setIs24HourView(true);

        binding.timePickerEntrada.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
            Calendar entrada = Calendar.getInstance();
            entrada.set(binding.datePickerEntrada.getYear(), binding.datePickerEntrada.getMonth(), binding.datePickerEntrada.getDayOfMonth(), hourOfDay, minute);
            Calendar salida = Calendar.getInstance();
            salida.set(binding.datePickerSalida.getYear(), binding.datePickerSalida.getMonth(), binding.datePickerSalida.getDayOfMonth(), binding.timePickerSalida.getHour(), binding.timePickerSalida.getMinute());
            // Si la salida es antes de la entrada, igualar
            if (salida.before(entrada)) {
                binding.datePickerSalida.updateDate(binding.datePickerEntrada.getYear(), binding.datePickerEntrada.getMonth(), binding.datePickerEntrada.getDayOfMonth());
                binding.timePickerSalida.setHour(hourOfDay);
                binding.timePickerSalida.setMinute(minute);
            }
            // Si la diferencia es mayor a 8 horas, ajustar salida
            long diffMillis = salida.getTimeInMillis() - entrada.getTimeInMillis();
            if (diffMillis > 8 * 60 * 60 * 1000) {
                Calendar nuevaSalida = (Calendar) entrada.clone();
                nuevaSalida.add(Calendar.HOUR_OF_DAY, 8);
                binding.datePickerSalida.updateDate(nuevaSalida.get(Calendar.YEAR), nuevaSalida.get(Calendar.MONTH), nuevaSalida.get(Calendar.DAY_OF_MONTH));
                binding.timePickerSalida.setHour(nuevaSalida.get(Calendar.HOUR_OF_DAY));
                binding.timePickerSalida.setMinute(nuevaSalida.get(Calendar.MINUTE));
                Toast.makeText(getContext(), "La reserva no puede superar las 8 horas", Toast.LENGTH_SHORT).show();
            }
        });
        binding.timePickerSalida.setOnTimeChangedListener((view12, hourOfDay, minute) -> {
            Calendar entrada = Calendar.getInstance();
            entrada.set(binding.datePickerEntrada.getYear(), binding.datePickerEntrada.getMonth(), binding.datePickerEntrada.getDayOfMonth(), binding.timePickerEntrada.getHour(), binding.timePickerEntrada.getMinute());
            Calendar salida = Calendar.getInstance();
            salida.set(binding.datePickerSalida.getYear(), binding.datePickerSalida.getMonth(), binding.datePickerSalida.getDayOfMonth(), hourOfDay, minute);
            // Si la salida es antes de la entrada, igualar
            if (salida.before(entrada)) {
                binding.datePickerSalida.updateDate(binding.datePickerEntrada.getYear(), binding.datePickerEntrada.getMonth(), binding.datePickerEntrada.getDayOfMonth());
                binding.timePickerSalida.setHour(binding.timePickerEntrada.getHour());
                binding.timePickerSalida.setMinute(binding.timePickerEntrada.getMinute());
            }
            // Si la diferencia es mayor a 8 horas, ajustar salida
            long diffMillis = salida.getTimeInMillis() - entrada.getTimeInMillis();
            if (diffMillis > 8 * 60 * 60 * 1000) {
                Calendar nuevaSalida = (Calendar) entrada.clone();
                nuevaSalida.add(Calendar.HOUR_OF_DAY, 8);
                binding.datePickerSalida.updateDate(nuevaSalida.get(Calendar.YEAR), nuevaSalida.get(Calendar.MONTH), nuevaSalida.get(Calendar.DAY_OF_MONTH));
                binding.timePickerSalida.setHour(nuevaSalida.get(Calendar.HOUR_OF_DAY));
                binding.timePickerSalida.setMinute(nuevaSalida.get(Calendar.MINUTE));
                Toast.makeText(getContext(), "La reserva no puede superar las 8 horas", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnContinuarReserva.setOnClickListener(v -> {
            if (selectedVehicle == null && !isEditMode) {
                Toast.makeText(getContext(), "No se ha recibido vehículo seleccionado", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si es edición, comprobar si la reserva está finalizada antes de permitir editar
            if (isEditMode) {
                reservasViewModel.getSelectedReserva().observe(getViewLifecycleOwner(), reservaObs -> {
                    if (reservaObs != null && reservaObs.getEstado() != null && !"pendiente".equalsIgnoreCase(reservaObs.getEstado())) {
                        Toast.makeText(getContext(), "Solo se puede editar una reserva pendiente (antes de su inicio)", Toast.LENGTH_LONG).show();
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

                    // Validar que la fecha seleccionada esté entre hoy y 7 días
                    Calendar hoy = Calendar.getInstance();
                    hoy.set(Calendar.HOUR_OF_DAY, 0);
                    hoy.set(Calendar.MINUTE, 0);
                    hoy.set(Calendar.SECOND, 0);
                    hoy.set(Calendar.MILLISECOND, 0);
                    Calendar maxFecha = (Calendar) hoy.clone();
                    maxFecha.add(Calendar.DAY_OF_YEAR, 7);
                    Calendar fechaSeleccionada = Calendar.getInstance();
                    fechaSeleccionada.set(binding.datePickerEntrada.getYear(), binding.datePickerEntrada.getMonth(), binding.datePickerEntrada.getDayOfMonth(), 0, 0, 0);
                    fechaSeleccionada.set(Calendar.MILLISECOND, 0);
                    if (fechaSeleccionada.before(hoy) || fechaSeleccionada.after(maxFecha)) {
                        Toast.makeText(getContext(), "Solo puedes reservar desde hoy hasta 7 días naturales.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Validar que la diferencia entre entrada y salida no supere 8 horas
                    long diffMillisHoras = fechaHoraSalida.getTimeInMillis() - fechaHoraEntrada.getTimeInMillis();
                    if (diffMillisHoras > 8 * 60 * 60 * 1000) {
                        Toast.makeText(getContext(), "La reserva no puede superar las 8 horas.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Formatear fechas y horas
                    int dayEntrada = binding.datePickerEntrada.getDayOfMonth();
                    int monthEntrada = binding.datePickerEntrada.getMonth() + 1;
                    int yearEntrada = binding.datePickerEntrada.getYear();
                    String fechaFormatted = String.format("%02d/%02d/%04d", dayEntrada, monthEntrada, yearEntrada);

                    // Calcular segundos desde medianoche para horaInicio y horaFin
                    Calendar medianoche = (Calendar) fechaHoraEntrada.clone();
                    medianoche.set(Calendar.HOUR_OF_DAY, 0);
                    medianoche.set(Calendar.MINUTE, 0);
                    medianoche.set(Calendar.SECOND, 0);
                    medianoche.set(Calendar.MILLISECOND, 0);
                    long horaInicio = (fechaHoraEntrada.getTimeInMillis() - medianoche.getTimeInMillis()) / 1000;
                    long horaFin = (fechaHoraSalida.getTimeInMillis() - medianoche.getTimeInMillis()) / 1000;
                    Hora hora = new Hora(horaInicio, horaFin);

                    // Confirmar edición y guardar cambios
                    reservaObs.setFecha(fechaFormatted);
                    reservaObs.setHoraInicio(hora);
                    reservasViewModel.updateReserva(reservaObs);
                    Toast.makeText(getContext(), "Reserva actualizada", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                });
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

            // Validar que la fecha seleccionada esté entre hoy y 7 días
            Calendar hoy = Calendar.getInstance();
            hoy.set(Calendar.HOUR_OF_DAY, 0);
            hoy.set(Calendar.MINUTE, 0);
            hoy.set(Calendar.SECOND, 0);
            hoy.set(Calendar.MILLISECOND, 0);
            Calendar maxFecha = (Calendar) hoy.clone();
            maxFecha.add(Calendar.DAY_OF_YEAR, 7);
            Calendar fechaSeleccionada = Calendar.getInstance();
            fechaSeleccionada.set(binding.datePickerEntrada.getYear(), binding.datePickerEntrada.getMonth(), binding.datePickerEntrada.getDayOfMonth(), 0, 0, 0);
            fechaSeleccionada.set(Calendar.MILLISECOND, 0);
            if (fechaSeleccionada.before(hoy) || fechaSeleccionada.after(maxFecha)) {
                Toast.makeText(getContext(), "Solo puedes reservar desde hoy hasta 7 días naturales.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar que la diferencia entre entrada y salida no supere 8 horas
            long diffMillisHoras = fechaHoraSalida.getTimeInMillis() - fechaHoraEntrada.getTimeInMillis();
            if (diffMillisHoras > 8 * 60 * 60 * 1000) {
                Toast.makeText(getContext(), "La reserva no puede superar las 8 horas.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Formatear fechas y horas
            int dayEntrada = binding.datePickerEntrada.getDayOfMonth();
            int monthEntrada = binding.datePickerEntrada.getMonth() + 1;
            int yearEntrada = binding.datePickerEntrada.getYear();
            String fechaFormatted = String.format("%02d/%02d/%04d", dayEntrada, monthEntrada, yearEntrada);

            // Calcular segundos desde medianoche para horaInicio y horaFin
            Calendar medianoche = (Calendar) fechaHoraEntrada.clone();
            medianoche.set(Calendar.HOUR_OF_DAY, 0);
            medianoche.set(Calendar.MINUTE, 0);
            medianoche.set(Calendar.SECOND, 0);
            medianoche.set(Calendar.MILLISECOND, 0);
            long horaInicio = (fechaHoraEntrada.getTimeInMillis() - medianoche.getTimeInMillis()) / 1000;
            long horaFin = (fechaHoraSalida.getTimeInMillis() - medianoche.getTimeInMillis()) / 1000;
            Hora hora = new Hora(horaInicio, horaFin);

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
                // Comprobar reservas solapadas antes de navegar
                reservasViewModel.loadUserReservas();
                reservasViewModel.getUserReservas().observe(getViewLifecycleOwner(), reservas -> {
                    boolean solapada = false;
                    if (reservas != null) {
                        for (com.lksnext.parkingplantilla.domain.Reserva r : reservas) {
                            if (r.getFecha() != null && r.getFecha().equals(fechaFormatted) && r.getHoraInicio() != null) {
                                long start1 = r.getHoraInicio().getHoraInicio();
                                long end1 = r.getHoraInicio().getHoraFin();
                                if (horaInicio < end1 && start1 < horaFin) {
                                    solapada = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (solapada) {
                        Toast.makeText(getContext(), "Ya tienes una reserva en ese rango de horas.", Toast.LENGTH_LONG).show();
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("selectedVehicle", selectedVehicle);
                        bundle.putString("fecha", fechaFormatted);
                        bundle.putLong("horaInicio", horaInicio);
                        bundle.putLong("horaFin", horaFin);
                        reservasViewModel.loadAvailablePlazas(fechaFormatted, hora);
                        Navigation.findNavController(requireView()).navigate(
                                com.lksnext.parkingplantilla.R.id.action_reservarFragment_to_seleccionarPlazaFragment,
                                bundle
                        );
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
