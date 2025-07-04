package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.domain.Hora;
import com.lksnext.parkingplantilla.domain.Plaza;
import com.lksnext.parkingplantilla.model.Vehicle;
import com.lksnext.parkingplantilla.viewmodel.ReservasViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SeleccionarPlazaFragment extends Fragment {

    private LinearLayout plazasContainer;
    private TextView tvFechaHora;
    private Button btnConfirmarReserva;
    private ReservasViewModel reservasViewModel;

    private Vehicle selectedVehicle;
    private String fecha;
    private long horaInicio;
    private long horaFin;
    private Plaza selectedPlaza;
    private boolean aparcarYa;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seleccionar_plaza, container, false);
        plazasContainer = view.findViewById(R.id.plazasContainer);
        tvFechaHora = view.findViewById(R.id.tvFechaHora);
        btnConfirmarReserva = view.findViewById(R.id.btnConfirmarReserva);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar toolbar
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Seleccionar Plaza");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        requireActivity().findViewById(R.id.mainToolbar).setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        // Inicializar ViewModel
        reservasViewModel = new ViewModelProvider(requireActivity()).get(ReservasViewModel.class);

        // Obtener argumentos
        if (getArguments() != null) {
            selectedVehicle = (Vehicle) getArguments().getSerializable("selectedVehicle");
            fecha = getArguments().getString("fecha");
            horaInicio = getArguments().getLong("horaInicio");
            horaFin = getArguments().getLong("horaFin");
            aparcarYa = getArguments().getBoolean("aparcarYa", false);

            // Formatear y mostrar la información de la reserva
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String horaInicioStr = sdf.format(new Date(horaInicio * 1000));
            String horaFinStr = sdf.format(new Date(horaFin * 1000));

            String tipoReserva = aparcarYa ? "Aparcar Ya" : "Reserva";
            tvFechaHora.setText(String.format("%s para %s\nEntrada: %s\nSalida: %s",
                    tipoReserva,
                    selectedVehicle.getName() + " (" + selectedVehicle.getLicensePlate() + ")",
                    horaInicioStr, horaFinStr));
        }

        // Observar plazas disponibles
        reservasViewModel.getAvailablePlazas().observe(getViewLifecycleOwner(), this::mostrarPlazasDisponibles);

        // Configurar botón de confirmación
        btnConfirmarReserva.setOnClickListener(v -> confirmarReserva());
        btnConfirmarReserva.setEnabled(false); // Deshabilitado hasta que se seleccione una plaza
    }

    private void mostrarPlazasDisponibles(List<Plaza> plazas) {
        plazasContainer.removeAllViews();

        if (plazas == null || plazas.isEmpty()) {
            TextView tvNoPlazas = new TextView(requireContext());
            tvNoPlazas.setText("No hay plazas disponibles para la fecha y hora seleccionadas");
            tvNoPlazas.setPadding(16, 16, 16, 16);
            plazasContainer.addView(tvNoPlazas);
            return;
        }

        // Filtrar plazas según el tipo de vehículo
        List<Plaza> plazasFiltradas = filtrarPlazasSegunVehiculo(plazas, selectedVehicle);

        if (plazasFiltradas.isEmpty()) {
            TextView tvNoPlazas = new TextView(requireContext());
            tvNoPlazas.setText("No hay plazas disponibles para este tipo de vehículo en la fecha y hora seleccionadas");
            tvNoPlazas.setPadding(16, 16, 16, 16);
            plazasContainer.addView(tvNoPlazas);
            return;
        }

        // Agrupar plazas por tipo
        LinearLayout plazasNormales = new LinearLayout(requireContext());
        plazasNormales.setOrientation(LinearLayout.VERTICAL);
        TextView tvNormales = new TextView(requireContext());
        tvNormales.setText("Plazas normales");
        tvNormales.setTextSize(18);
        tvNormales.setPadding(16, 32, 16, 16);
        plazasNormales.addView(tvNormales);

        LinearLayout plazasDiscapacitados = new LinearLayout(requireContext());
        plazasDiscapacitados.setOrientation(LinearLayout.VERTICAL);
        TextView tvDiscapacitados = new TextView(requireContext());
        tvDiscapacitados.setText("Plazas para discapacitados");
        tvDiscapacitados.setTextSize(18);
        tvDiscapacitados.setPadding(16, 32, 16, 16);
        plazasDiscapacitados.addView(tvDiscapacitados);

        LinearLayout plazasElectricos = new LinearLayout(requireContext());
        plazasElectricos.setOrientation(LinearLayout.VERTICAL);
        TextView tvElectricos = new TextView(requireContext());
        tvElectricos.setText("Plazas para vehículos eléctricos");
        tvElectricos.setTextSize(18);
        tvElectricos.setPadding(16, 32, 16, 16);
        plazasElectricos.addView(tvElectricos);

        LinearLayout plazasMotos = new LinearLayout(requireContext());
        plazasMotos.setOrientation(LinearLayout.VERTICAL);
        TextView tvMotos = new TextView(requireContext());
        tvMotos.setText("Plazas para motos");
        tvMotos.setTextSize(18);
        tvMotos.setPadding(16, 32, 16, 16);
        plazasMotos.addView(tvMotos);

        // Crear grid de botones para cada tipo de plaza
        LinearLayout gridNormales = new LinearLayout(requireContext());
        gridNormales.setOrientation(LinearLayout.HORIZONTAL);
        gridNormales.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        plazasNormales.addView(gridNormales);

        LinearLayout gridDiscapacitados = new LinearLayout(requireContext());
        gridDiscapacitados.setOrientation(LinearLayout.HORIZONTAL);
        gridDiscapacitados.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        plazasDiscapacitados.addView(gridDiscapacitados);

        LinearLayout gridElectricos = new LinearLayout(requireContext());
        gridElectricos.setOrientation(LinearLayout.HORIZONTAL);
        gridElectricos.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        plazasElectricos.addView(gridElectricos);

        LinearLayout gridMotos = new LinearLayout(requireContext());
        gridMotos.setOrientation(LinearLayout.HORIZONTAL);
        gridMotos.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        plazasMotos.addView(gridMotos);

        // Contador para crear filas en el grid
        int contadorNormales = 0;
        int contadorDiscapacitados = 0;
        int contadorElectricos = 0;
        int contadorMotos = 0;

        // Crear botones para cada plaza
        for (Plaza plaza : plazasFiltradas) {
            Button btnPlaza = new Button(requireContext());
            btnPlaza.setText(String.valueOf(plaza.getId()));
            btnPlaza.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f));

            // Estilo según tipo de plaza
            if ("normal".equals(plaza.getTipo())) {
                btnPlaza.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light, requireContext().getTheme()));
                if (contadorNormales % 4 == 0) {
                    gridNormales = new LinearLayout(requireContext());
                    gridNormales.setOrientation(LinearLayout.HORIZONTAL);
                    gridNormales.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    plazasNormales.addView(gridNormales);
                }
                gridNormales.addView(btnPlaza);
                contadorNormales++;
            } else if ("discapacitados".equals(plaza.getTipo())) {
                btnPlaza.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light, requireContext().getTheme()));
                if (contadorDiscapacitados % 4 == 0) {
                    gridDiscapacitados = new LinearLayout(requireContext());
                    gridDiscapacitados.setOrientation(LinearLayout.HORIZONTAL);
                    gridDiscapacitados.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    plazasDiscapacitados.addView(gridDiscapacitados);
                }
                gridDiscapacitados.addView(btnPlaza);
                contadorDiscapacitados++;
            } else if ("eléctricos".equals(plaza.getTipo())) {
                btnPlaza.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light, requireContext().getTheme()));
                if (contadorElectricos % 4 == 0) {
                    gridElectricos = new LinearLayout(requireContext());
                    gridElectricos.setOrientation(LinearLayout.HORIZONTAL);
                    gridElectricos.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    plazasElectricos.addView(gridElectricos);
                }
                gridElectricos.addView(btnPlaza);
                contadorElectricos++;
            } else if ("moto".equals(plaza.getTipo())) {
                btnPlaza.setBackgroundColor(getResources().getColor(android.R.color.holo_purple, requireContext().getTheme()));
                if (contadorMotos % 4 == 0) {
                    gridMotos = new LinearLayout(requireContext());
                    gridMotos.setOrientation(LinearLayout.HORIZONTAL);
                    gridMotos.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    plazasMotos.addView(gridMotos);
                }
                gridMotos.addView(btnPlaza);
                contadorMotos++;
            }

            final Plaza plazaSeleccionada = plaza;
            btnPlaza.setOnClickListener(v -> {
                // Deseleccionar todas las plazas
                for (int i = 0; i < plazasContainer.getChildCount(); i++) {
                    View child = plazasContainer.getChildAt(i);
                    if (child instanceof LinearLayout) {
                        LinearLayout layout = (LinearLayout) child;
                        for (int j = 1; j < layout.getChildCount(); j++) { // Empezar desde 1 para saltar el TextView
                            View subChild = layout.getChildAt(j);
                            if (subChild instanceof LinearLayout) {
                                LinearLayout subLayout = (LinearLayout) subChild;
                                for (int k = 0; k < subLayout.getChildCount(); k++) {
                                    View button = subLayout.getChildAt(k);
                                    if (button instanceof Button) {
                                        button.setAlpha(1.0f);
                                    }
                                }
                            }
                        }
                    }
                }

                // Resaltar la plaza seleccionada
                btnPlaza.setAlpha(0.5f);
                selectedPlaza = plazaSeleccionada;
                btnConfirmarReserva.setEnabled(true);

                Toast.makeText(requireContext(), "Plaza " + plaza.getId() + " seleccionada", Toast.LENGTH_SHORT).show();
            });
        }

        // Añadir las secciones al contenedor principal
        if (contadorNormales > 0) {
            plazasContainer.addView(plazasNormales);
        }
        if (contadorDiscapacitados > 0) {
            plazasContainer.addView(plazasDiscapacitados);
        }
        if (contadorElectricos > 0) {
            plazasContainer.addView(plazasElectricos);
        }
        if (contadorMotos > 0) {
            plazasContainer.addView(plazasMotos);
        }
    }

    /**
     * Filtra las plazas disponibles según el tipo de vehículo
     */
    private List<Plaza> filtrarPlazasSegunVehiculo(List<Plaza> plazasDisponibles, Vehicle vehiculo) {
        List<Plaza> plazasFiltradas = new ArrayList<>();

        for (Plaza plaza : plazasDisponibles) {
            // Filtro para vehículos eléctricos
            if ("eléctricos".equals(plaza.getTipo()) && !vehiculo.isElectric()) {
                continue; // Saltar esta plaza si no es un vehículo eléctrico
            }

            // Filtro para plazas de discapacitados
            if ("discapacitados".equals(plaza.getTipo()) && !vehiculo.isForDisabled()) {
                continue; // Saltar esta plaza si no es un vehículo para discapacitados
            }

            // Filtro para plazas de moto
            if ("moto".equals(plaza.getTipo()) && vehiculo.getType() != Vehicle.VehicleType.MOTORCYCLE) {
                continue; // Saltar esta plaza si no es una moto
            }

            // Los coches no pueden aparcar en plazas de moto
            if (vehiculo.getType() == Vehicle.VehicleType.MOTORCYCLE ||
                    !"moto".equals(plaza.getTipo())) {
                plazasFiltradas.add(plaza);
            }
        }

        return plazasFiltradas;
    }

    private void confirmarReserva() {
        if (selectedPlaza == null) {
            Toast.makeText(requireContext(), "Selecciona una plaza primero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar autenticación a través del ViewModel
        if (!reservasViewModel.isUserAuthenticated()) {
            Toast.makeText(requireContext(), "No hay usuario autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear objeto Hora
        Hora hora = new Hora(horaInicio, horaFin);

        // Crear reserva - ya no pasamos el userId porque el ViewModel lo obtiene del Repository
        reservasViewModel.createReserva(fecha, selectedPlaza, hora);

        // Observar el resultado
        reservasViewModel.isReservaCreated().observe(getViewLifecycleOwner(), isCreated -> {
            if (isCreated != null && isCreated) {
                Toast.makeText(requireContext(), "Reserva creada con éxito", Toast.LENGTH_LONG).show();
                // Navegar a mis reservas
                Navigation.findNavController(requireView()).navigate(
                        R.id.action_seleccionarPlazaFragment_to_misReservasFragment);
            }
        });

        reservasViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
