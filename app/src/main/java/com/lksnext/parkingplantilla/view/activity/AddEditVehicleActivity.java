package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.ActivityAddEditVehicleBinding;
import com.lksnext.parkingplantilla.model.Vehicle;

import java.util.UUID;

public class AddEditVehicleActivity extends AppCompatActivity {

    public static final String EXTRA_VEHICLE = "extra_vehicle";

    private ActivityAddEditVehicleBinding binding;
    private Vehicle editingVehicle;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditVehicleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Comprobar si estamos en modo edición
        if (getIntent().hasExtra(EXTRA_VEHICLE)) {
            editingVehicle = (Vehicle) getIntent().getSerializableExtra(EXTRA_VEHICLE);
            isEditMode = true;
            fillFormWithVehicleData();
            getSupportActionBar().setTitle("Editar Vehículo");
            binding.btnEliminar.setVisibility(View.VISIBLE);
        } else {
            editingVehicle = new Vehicle();
            getSupportActionBar().setTitle("Añadir Vehículo");
            binding.btnEliminar.setVisibility(View.GONE);
        }

        // Configurar listeners
        binding.btnGuardar.setOnClickListener(v -> saveVehicle());
        binding.btnEliminar.setOnClickListener(v -> showDeleteDialog());
    }

    private void fillFormWithVehicleData() {
        binding.edtNombre.setText(editingVehicle.getName());
        binding.edtMatricula.setText(editingVehicle.getLicensePlate());
        binding.edtMarca.setText(editingVehicle.getBrand());
        binding.edtModelo.setText(editingVehicle.getModel());

        // Seleccionar el tipo de vehículo correcto
        switch (editingVehicle.getType()) {
            case CAR:
                binding.rbCoche.setChecked(true);
                break;
            case MOTORCYCLE:
                binding.rbMoto.setChecked(true);
                break;
            case VAN:
                binding.rbFurgoneta.setChecked(true);
                break;
        }
    }

    private void saveVehicle() {
        // Validar formulario
        if (!validateForm()) {
            return;
        }

        // Obtener los datos del formulario
        String nombre = binding.edtNombre.getText().toString().trim();
        String matricula = binding.edtMatricula.getText().toString().trim();
        String marca = binding.edtMarca.getText().toString().trim();
        String modelo = binding.edtModelo.getText().toString().trim();

        // Determinar el tipo de vehículo seleccionado
        Vehicle.VehicleType tipo;
        int selectedRadioButtonId = binding.rgTipoVehiculo.getCheckedRadioButtonId();

        if (selectedRadioButtonId == R.id.rbCoche) {
            tipo = Vehicle.VehicleType.CAR;
        } else if (selectedRadioButtonId == R.id.rbMoto) {
            tipo = Vehicle.VehicleType.MOTORCYCLE;
        } else {
            tipo = Vehicle.VehicleType.VAN;
        }

        // Si es nuevo vehículo, generar ID
        if (!isEditMode) {
            editingVehicle.setId(UUID.randomUUID().toString());
        }

        // Actualizar objeto vehículo
        editingVehicle.setName(nombre);
        editingVehicle.setLicensePlate(matricula);
        editingVehicle.setBrand(marca);
        editingVehicle.setModel(modelo);
        editingVehicle.setType(tipo);

        // Devolver resultado a la actividad anterior
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_VEHICLE, editingVehicle);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validar nombre
        if (binding.edtNombre.getText().toString().trim().isEmpty()) {
            binding.tilNombre.setError("Introduce un nombre para el vehículo");
            isValid = false;
        } else {
            binding.tilNombre.setError(null);
        }

        // Validar matrícula
        String matricula = binding.edtMatricula.getText().toString().trim();
        if (matricula.isEmpty()) {
            binding.tilMatricula.setError("Introduce la matrícula");
            isValid = false;
        } else {
            binding.tilMatricula.setError(null);
        }

        // Validar marca
        if (binding.edtMarca.getText().toString().trim().isEmpty()) {
            binding.tilMarca.setError("Introduce la marca");
            isValid = false;
        } else {
            binding.tilMarca.setError(null);
        }

        // Validar modelo
        if (binding.edtModelo.getText().toString().trim().isEmpty()) {
            binding.tilModelo.setError("Introduce el modelo");
            isValid = false;
        } else {
            binding.tilModelo.setError(null);
        }

        return isValid;
    }

    private void showDeleteDialog() {
        DeleteVehicleDialogFragment dialog = DeleteVehicleDialogFragment.newInstance(editingVehicle);
        dialog.setDeleteVehicleListener(new DeleteVehicleDialogFragment.DeleteVehicleListener() {
            @Override
            public void onDeleteConfirmed(Vehicle vehicle) {
                // Devolver resultado para eliminar
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_VEHICLE, editingVehicle);
                resultIntent.putExtra("delete", true);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        dialog.show(getSupportFragmentManager(), "DeleteVehicleDialog");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
