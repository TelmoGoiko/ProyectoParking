package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.adapter.ReservasAdapter;
import com.lksnext.parkingplantilla.databinding.FragmentMisReservasBinding;
import com.lksnext.parkingplantilla.domain.Reserva;
import com.lksnext.parkingplantilla.viewmodel.ReservasViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MisReservasFragment extends Fragment {

    private FragmentMisReservasBinding binding;
    private ReservasAdapter reservasAdapter;
    private ReservasViewModel reservasViewModel;

    // Mapa para guardar la relación entre las reservas del modelo y sus IDs en la base de datos
    private Map<com.lksnext.parkingplantilla.model.Reserva, String> reservasIdMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMisReservasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Use the shared Toolbar from MainActivity
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Mis Reservas");
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        requireActivity().findViewById(R.id.mainToolbar).setOnClickListener(v -> requireActivity().onBackPressed());

        // Inicializar ViewModel
        reservasViewModel = new ViewModelProvider(requireActivity()).get(ReservasViewModel.class);

        // Configurar el RecyclerView
        binding.recyclerViewReservas.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configurar adaptador vacío inicialmente
        reservasAdapter = new ReservasAdapter(new ArrayList<>(),
            // OnClick listener para ver detalles o editar
            reserva -> {
                // Obtener el ID real de la reserva desde nuestro mapa
                String reservaId = reservasIdMap.get(reserva);
                if (reservaId != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("reservaId", reservaId);
                    // Navegar a ReservarFragment pasando el ID de la reserva
                    androidx.navigation.Navigation.findNavController(requireView())
                        .navigate(R.id.action_misReservasFragment_to_reservarFragment, bundle);
                } else {
                    Toast.makeText(getContext(), "Error al identificar la reserva", Toast.LENGTH_SHORT).show();
                }
            },
            // OnClick listener para eliminar
            reserva -> {
                // Mostrar diálogo de confirmación
                new AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar reserva")
                    .setMessage("¿Estás seguro de que quieres eliminar esta reserva?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        // Obtener el ID real de la reserva desde nuestro mapa
                        String reservaId = reservasIdMap.get(reserva);
                        if (reservaId != null) {
                            // Eliminar la reserva usando el ViewModel (que a su vez usará el Repository)
                            eliminarReserva(reservaId);
                        } else {
                            Toast.makeText(requireContext(), "Error al identificar la reserva", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
                return true;
            });

        binding.recyclerViewReservas.setAdapter(reservasAdapter);

        // Observar las reservas del usuario
        cargarReservasDeUsuario();
    }

    private void cargarReservasDeUsuario() {
        // Verificar si hay un usuario autenticado usando el ViewModel
        if (!reservasViewModel.isUserAuthenticated()) {
            Toast.makeText(getContext(), "No hay usuario autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar loading
        binding.progressBar.setVisibility(View.VISIBLE);

        // Cargar reservas del usuario desde el ViewModel
        reservasViewModel.loadUserReservas();

        // Observar cambios en las reservas
        reservasViewModel.getUserReservas().observe(getViewLifecycleOwner(), reservasDominio -> {
            binding.progressBar.setVisibility(View.GONE);

            if (reservasDominio == null || reservasDominio.isEmpty()) {
                binding.tvNoReservas.setVisibility(View.VISIBLE);
                binding.recyclerViewReservas.setVisibility(View.GONE);
                return;
            }

            binding.tvNoReservas.setVisibility(View.GONE);
            binding.recyclerViewReservas.setVisibility(View.VISIBLE);

            // Convertir las reservas de dominio a modelo para el adaptador
            List<com.lksnext.parkingplantilla.model.Reserva> reservasModelo = new ArrayList<>();

            // Mapeo de IDs de reservas para poder eliminarlas luego
            reservasIdMap = new HashMap<>();

            for (Reserva reservaDominio : reservasDominio) {
                // Formatear fechas y horas para mostrar
                SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm", Locale.getDefault());

                String nombreParking = "Plaza " + (reservaDominio.getPlazaId() != null ?
                        reservaDominio.getPlazaId().getId() : "N/A");

                String fecha = reservaDominio.getFecha();

                String horaInicio = "N/A";
                String horaFin = "N/A";

                if (reservaDominio.getHoraInicio() != null) {
                    try {
                        SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date fechaBase = sdfFecha.parse(fecha);
                        Date dateInicio = new Date(fechaBase.getTime() + reservaDominio.getHoraInicio().getHoraInicio() * 1000);
                        Date dateFin = new Date(fechaBase.getTime() + reservaDominio.getHoraInicio().getHoraFin() * 1000);
                        horaInicio = sdfHora.format(dateInicio);
                        horaFin = sdfHora.format(dateFin);
                    } catch (java.text.ParseException e) {
                        horaInicio = "?";
                        horaFin = "?";
                    }
                }

                String estado = reservaDominio.getEstado() != null ? reservaDominio.getEstado() : "Confirmada"; // Por defecto todas las reservas están confirmadas

                com.lksnext.parkingplantilla.model.Reserva reservaModelo =
                    new com.lksnext.parkingplantilla.model.Reserva(
                        nombreParking, fecha, horaInicio, horaFin, estado);

                reservasModelo.add(reservaModelo);

                // Guardar mapeo entre la reserva del modelo y su ID real
                reservasIdMap.put(reservaModelo, reservaDominio.getId());
            }

            // Actualizar adaptador con las nuevas reservas
            reservasAdapter.actualizarReservas(reservasModelo);
        });
    }

    private void eliminarReserva(String plazaId) {
        // Lógica para eliminar la reserva
        // Esto debería llamar al método correspondiente en el ViewModel
        reservasViewModel.eliminarReserva(plazaId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
