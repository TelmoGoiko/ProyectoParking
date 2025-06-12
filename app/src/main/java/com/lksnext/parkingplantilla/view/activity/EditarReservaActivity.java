package com.lksnext.parkingplantilla.view.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityEditarReservaBinding;
import com.lksnext.parkingplantilla.model.Reserva;

public class EditarReservaActivity extends AppCompatActivity {

    private ActivityEditarReservaBinding binding;
    private Reserva reserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditarReservaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtener la reserva pasada como extra
        if (getIntent().hasExtra("RESERVA")) {
            reserva = (Reserva) getIntent().getSerializableExtra("RESERVA");
            cargarDatosReserva();
        }

        // Configurar el botón de volver
        binding.btnVolver.setOnClickListener(v -> finish());

        // Configurar el botón de guardar cambios
        binding.btnGuardar.setOnClickListener(v -> {
            // Aquí iría la lógica para guardar los cambios
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Configurar el botón de eliminar
        binding.btnEliminar.setOnClickListener(v -> mostrarDialogoEliminar());
    }

    private void cargarDatosReserva() {
        if (reserva != null) {
            binding.tvNombreParking.setText(reserva.getNombreParking());
            binding.tvFecha.setText(reserva.getFecha());
            binding.etHoraInicio.setText(reserva.getHoraInicio());
            binding.etHoraFin.setText(reserva.getHoraFin());
            binding.tvEstado.setText(reserva.getEstado());
        }
    }

    private void mostrarDialogoEliminar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.eliminar_reserva_titulo);
        builder.setMessage(R.string.eliminar_reserva_mensaje);

        builder.setPositiveButton(R.string.si, (dialog, which) -> {
            // Aquí iría la lógica para eliminar la reserva
            Toast.makeText(this, "Reserva eliminada", Toast.LENGTH_SHORT).show();
            finish();
        });

        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
