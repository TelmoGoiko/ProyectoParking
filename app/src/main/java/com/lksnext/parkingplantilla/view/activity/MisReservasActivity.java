package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.adapter.ReservasAdapter;
import com.lksnext.parkingplantilla.databinding.ActivityMisReservasBinding;
import com.lksnext.parkingplantilla.model.Reserva;

import java.util.ArrayList;
import java.util.List;

public class MisReservasActivity extends AppCompatActivity {

    private ActivityMisReservasBinding binding;
    private ReservasAdapter reservasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMisReservasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar el botón de volver
        binding.btnVolver.setOnClickListener(v -> finish());

        // Configurar el RecyclerView
        binding.recyclerViewReservas.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar el adaptador con datos de prueba
        cargarDatosDePrueba();
    }

    private void cargarDatosDePrueba() {
        // Datos de prueba para el RecyclerView
        List<Reserva> reservas = new ArrayList<>();
        reservas.add(new Reserva("Parking Centro", "12/06/2025", "14:00", "16:00", "Confirmada"));
        reservas.add(new Reserva("Parking Norte", "15/06/2025", "09:00", "11:00", "Pendiente"));
        reservas.add(new Reserva("Parking Sur", "20/06/2025", "18:00", "20:00", "Confirmada"));

        // Configurar el adaptador con un listener para los botones de cada reserva
        reservasAdapter = new ReservasAdapter(reservas,
            // Listener para el botón de editar
            reserva -> {
                Intent intent = new Intent(this, EditarReservaActivity.class);
                intent.putExtra("RESERVA", reserva);
                startActivity(intent);
            },
            // Listener para el botón de eliminar
            reserva -> mostrarDialogoEliminar(reserva)
        );

        binding.recyclerViewReservas.setAdapter(reservasAdapter);

        // Mostrar mensaje si no hay reservas
        if (reservas.isEmpty()) {
            binding.tvNoReservas.setVisibility(View.VISIBLE);
            binding.recyclerViewReservas.setVisibility(View.GONE);
        } else {
            binding.tvNoReservas.setVisibility(View.GONE);
            binding.recyclerViewReservas.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarDialogoEliminar(Reserva reserva) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.eliminar_reserva_titulo);
        builder.setMessage(R.string.eliminar_reserva_mensaje);

        builder.setPositiveButton(R.string.si, (dialog, which) -> {
            // Aquí iría la lógica para eliminar la reserva
            Toast.makeText(this, "Reserva eliminada", Toast.LENGTH_SHORT).show();

            // Actualizar la lista después de eliminar
            List<Reserva> reservasActuales = new ArrayList<>(reservasAdapter.getReservas());
            reservasActuales.remove(reserva);
            reservasAdapter.actualizarReservas(reservasActuales);

            // Mostrar mensaje si no hay reservas
            if (reservasActuales.isEmpty()) {
                binding.tvNoReservas.setVisibility(View.VISIBLE);
                binding.recyclerViewReservas.setVisibility(View.GONE);
            }
        });

        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
