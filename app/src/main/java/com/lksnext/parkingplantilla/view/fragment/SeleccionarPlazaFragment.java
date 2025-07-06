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

    private LinearLayout seccionCoche, seccionCocheElectrico, seccionCocheMinusvalido, seccionCocheMinusvalidoElectrico, seccionMoto, seccionMotoMinusvalida;
    private LinearLayout containerCoche, containerCocheElectrico, containerCocheMinusvalido, containerCocheMinusvalidoElectrico, containerMoto, containerMotoMinusvalida;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seleccionar_plaza, container, false);
        plazasContainer = view.findViewById(R.id.plazasContainer);
        tvFechaHora = view.findViewById(R.id.tvFechaHora);
        btnConfirmarReserva = view.findViewById(R.id.btnConfirmarReserva);
        seccionCoche = view.findViewById(R.id.seccionCoche);
        seccionCocheElectrico = view.findViewById(R.id.seccionCocheElectrico);
        seccionCocheMinusvalido = view.findViewById(R.id.seccionCocheMinusvalido);
        seccionCocheMinusvalidoElectrico = view.findViewById(R.id.seccionCocheMinusvalidoElectrico);
        seccionMoto = view.findViewById(R.id.seccionMoto);
        seccionMotoMinusvalida = view.findViewById(R.id.seccionMotoMinusvalida);
        containerCoche = view.findViewById(R.id.containerCoche);
        containerCocheElectrico = view.findViewById(R.id.containerCocheElectrico);
        containerCocheMinusvalido = view.findViewById(R.id.containerCocheMinusvalido);
        containerCocheMinusvalidoElectrico = view.findViewById(R.id.containerCocheMinusvalidoElectrico);
        containerMoto = view.findViewById(R.id.containerMoto);
        containerMotoMinusvalida = view.findViewById(R.id.containerMotoMinusvalida);
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

            // Formatear y mostrar la información de la reserva correctamente
            SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fechaBase = null;
            try {
                fechaBase = sdfFecha.parse(fecha); // fecha es "dd/MM/yyyy"
            } catch (java.text.ParseException e) {
                fechaBase = new Date(); // fallback: hoy
            }
            Date horaInicioDate = new Date(fechaBase.getTime() + horaInicio * 1000);
            Date horaFinDate = new Date(fechaBase.getTime() + horaFin * 1000);
            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String horaInicioStr = sdfHora.format(horaInicioDate);
            String horaFinStr = sdfHora.format(horaFinDate);

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
        // Limpiar contenedores y ocultar secciones
        containerCoche.removeAllViews();
        containerCocheElectrico.removeAllViews();
        containerCocheMinusvalido.removeAllViews();
        containerCocheMinusvalidoElectrico.removeAllViews();
        containerMoto.removeAllViews();
        containerMotoMinusvalida.removeAllViews();
        seccionCoche.setVisibility(View.GONE);
        seccionCocheElectrico.setVisibility(View.GONE);
        seccionCocheMinusvalido.setVisibility(View.GONE);
        seccionCocheMinusvalidoElectrico.setVisibility(View.GONE);
        seccionMoto.setVisibility(View.GONE);
        seccionMotoMinusvalida.setVisibility(View.GONE);
        plazasContainer.removeAllViews(); // Para mensajes de error

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

        // Agrupar y mostrar plazas por tipo
        for (Plaza plaza : plazasFiltradas) {
            Button btnPlaza = new Button(requireContext());
            btnPlaza.setText(String.valueOf(plaza.getId()));
            btnPlaza.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f));
            // Asignar color y contenedor según tipo
            LinearLayout targetContainer = null;
            LinearLayout targetSection = null;
            switch (plaza.getTipo()) {
                case "normal":
                    btnPlaza.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light, requireContext().getTheme()));
                    targetContainer = containerCoche;
                    targetSection = seccionCoche;
                    break;
                case "electrico":
                    btnPlaza.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light, requireContext().getTheme()));
                    targetContainer = containerCocheElectrico;
                    targetSection = seccionCocheElectrico;
                    break;
                case "minusvalido":
                    btnPlaza.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light, requireContext().getTheme()));
                    targetContainer = containerCocheMinusvalido;
                    targetSection = seccionCocheMinusvalido;
                    break;
                case "electrico_minusvalido":
                    btnPlaza.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light, requireContext().getTheme()));
                    targetContainer = containerCocheMinusvalidoElectrico;
                    targetSection = seccionCocheMinusvalidoElectrico;
                    break;
                case "moto":
                    btnPlaza.setBackgroundColor(getResources().getColor(android.R.color.holo_purple, requireContext().getTheme()));
                    targetContainer = containerMoto;
                    targetSection = seccionMoto;
                    break;
                case "moto_minusvalido":
                    btnPlaza.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, requireContext().getTheme()));
                    targetContainer = containerMotoMinusvalida;
                    targetSection = seccionMotoMinusvalida;
                    break;
            }
            if (targetContainer != null && targetSection != null) {
                targetContainer.addView(btnPlaza);
                targetSection.setVisibility(View.VISIBLE);
            }
            final Plaza plazaSeleccionada = plaza;
            btnPlaza.setOnClickListener(v -> {
                // Comprobación extra: ¿la plaza sigue disponible?
                if (!plazasFiltradas.contains(plazaSeleccionada)) {
                    Toast.makeText(requireContext(), "Esta plaza ya no está disponible", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Deseleccionar todos los botones
                deseleccionarTodasLasPlazas();
                btnPlaza.setAlpha(0.5f);
                selectedPlaza = plazaSeleccionada;
                btnConfirmarReserva.setEnabled(true);
            });
        }
    }

    private void deseleccionarTodasLasPlazas() {
        LinearLayout[] containers = {containerCoche, containerCocheElectrico, containerCocheMinusvalido, containerCocheMinusvalidoElectrico, containerMoto, containerMotoMinusvalida};
        for (LinearLayout container : containers) {
            for (int i = 0; i < container.getChildCount(); i++) {
                View v = container.getChildAt(i);
                if (v instanceof Button) v.setAlpha(1.0f);
            }
        }
    }

    /**
     * Filtra las plazas disponibles según el tipo de vehículo
     */
    private List<Plaza> filtrarPlazasSegunVehiculo(List<Plaza> plazasDisponibles, Vehicle vehiculo) {
        List<Plaza> plazasFiltradas = new ArrayList<>();
        Vehicle.VehicleType tipoVehiculo = vehiculo.getType();
        boolean esElectrico = vehiculo.isElectric();
        boolean esMinusvalido = vehiculo.isForDisabled();

        for (Plaza plaza : plazasDisponibles) {
            String tipoPlaza = plaza.getTipo();
            switch (tipoVehiculo) {
                case CAR:
                    if (esElectrico && esMinusvalido) {
                        // Coche minusválido eléctrico
                        if (tipoPlaza.equals("electrico_minusvalido") || tipoPlaza.equals("electrico") || tipoPlaza.equals("minusvalido") || tipoPlaza.equals("normal")) {
                            plazasFiltradas.add(plaza);
                        }
                    } else if (esElectrico) {
                        // Coche eléctrico
                        if (tipoPlaza.equals("electrico") || tipoPlaza.equals("normal")) {
                            plazasFiltradas.add(plaza);
                        }
                    } else if (esMinusvalido) {
                        // Coche minusválido
                        if (tipoPlaza.equals("minusvalido") || tipoPlaza.equals("normal")) {
                            plazasFiltradas.add(plaza);
                        }
                    } else {
                        // Coche normal
                        if (tipoPlaza.equals("normal")) {
                            plazasFiltradas.add(plaza);
                        }
                    }
                    break;
                case MOTORCYCLE:
                    if (esMinusvalido) {
                        // Moto minusválida
                        if (tipoPlaza.equals("moto_minusvalida") || tipoPlaza.equals("moto")) {
                            plazasFiltradas.add(plaza);
                        }
                    } else {
                        // Moto normal
                        if (tipoPlaza.equals("moto")) {
                            plazasFiltradas.add(plaza);
                        }
                    }
                    break;
                default:
                    // Otros tipos de vehículo, no permitidos
                    break;
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

        // Crear reserva pasando el vehicleId correcto
        reservasViewModel.createReserva(fecha, selectedPlaza, hora, selectedVehicle != null ? selectedVehicle.getId() : null);

        // Observar el resultado
        reservasViewModel.isReservaCreated().observe(getViewLifecycleOwner(), isCreated -> {
            if (isCreated != null && isCreated) {
                // Solo mostrar éxito si no hay error
                String error = reservasViewModel.getErrorMessage().getValue();
                if (error == null || error.isEmpty()) {
                    Toast.makeText(requireContext(), "Reserva creada con éxito", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(requireView()).navigate(R.id.action_seleccionarPlazaFragment_to_misReservasFragment);
                }
            }
        });

        reservasViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
