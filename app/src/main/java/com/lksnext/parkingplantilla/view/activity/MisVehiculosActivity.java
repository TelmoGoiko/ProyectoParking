package com.lksnext.parkingplantilla.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.adapter.VehicleAdapter;
import com.lksnext.parkingplantilla.databinding.ActivityMisVehiculosBinding;
import com.lksnext.parkingplantilla.model.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MisVehiculosActivity extends AppCompatActivity implements VehicleAdapter.OnVehicleClickListener {

    private ActivityMisVehiculosBinding binding;
    private VehicleAdapter adapter;

    // Lista temporal para almacenar vehículos (en una app real, estos vendrían de una base de datos)
    private List<Vehicle> vehicleList = new ArrayList<>();

    private static final int REQUEST_ADD_VEHICLE = 1;
    private static final int REQUEST_EDIT_VEHICLE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMisVehiculosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configurar RecyclerView
        binding.recyclerViewVehiculos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VehicleAdapter(this);
        binding.recyclerViewVehiculos.setAdapter(adapter);

        // Configurar botón para añadir vehículo
        binding.btnAddVehiculo.setOnClickListener(v -> {
            Intent intent = new Intent(MisVehiculosActivity.this, AddEditVehicleActivity.class);
            startActivityForResult(intent, REQUEST_ADD_VEHICLE);
        });

        // Cargar vehículos de ejemplo (en una app real, aquí se cargarían de la base de datos)
        loadSampleVehicles();
        updateUI();
    }

    private void loadSampleVehicles() {
        // Solo crear datos de ejemplo si la lista está vacía
        if (vehicleList.isEmpty()) {
            // Ejemplo de vehículo 1
            Vehicle vehicle1 = new Vehicle(
                    UUID.randomUUID().toString(),
                    "Mi Coche",
                    "1234ABC",
                    "Seat",
                    "León",
                    Vehicle.VehicleType.CAR
            );

            // Ejemplo de vehículo 2
            Vehicle vehicle2 = new Vehicle(
                    UUID.randomUUID().toString(),
                    "Mi Moto",
                    "5678DEF",
                    "Honda",
                    "CBR",
                    Vehicle.VehicleType.MOTORCYCLE
            );

            vehicleList.add(vehicle1);
            vehicleList.add(vehicle2);
        }
    }

    private void updateUI() {
        if (vehicleList.isEmpty()) {
            binding.tvEmptyVehiculos.setVisibility(View.VISIBLE);
            binding.recyclerViewVehiculos.setVisibility(View.GONE);
        } else {
            binding.tvEmptyVehiculos.setVisibility(View.GONE);
            binding.recyclerViewVehiculos.setVisibility(View.VISIBLE);
            adapter.setVehicles(vehicleList);
        }
    }

    @Override
    public void onVehicleClick(Vehicle vehicle) {
        // Opcionalmente, mostrar detalles del vehículo o editar directamente
        Intent intent = new Intent(this, AddEditVehicleActivity.class);
        intent.putExtra(AddEditVehicleActivity.EXTRA_VEHICLE, vehicle);
        startActivityForResult(intent, REQUEST_EDIT_VEHICLE);
    }

    @Override
    public void onEditClick(Vehicle vehicle) {
        Intent intent = new Intent(this, AddEditVehicleActivity.class);
        intent.putExtra(AddEditVehicleActivity.EXTRA_VEHICLE, vehicle);
        startActivityForResult(intent, REQUEST_EDIT_VEHICLE);
    }

    @Override
    public void onDeleteClick(Vehicle vehicle) {
        showDeleteDialog(vehicle);
    }

    private void showDeleteDialog(Vehicle vehicle) {
        DeleteVehicleDialogFragment dialog = DeleteVehicleDialogFragment.newInstance(vehicle);
        dialog.setDeleteVehicleListener(new DeleteVehicleDialogFragment.DeleteVehicleListener() {
            @Override
            public void onDeleteConfirmed(Vehicle vehicle) {
                // Eliminar vehículo de la lista
                vehicleList.remove(vehicle);
                updateUI();
                Toast.makeText(MisVehiculosActivity.this, "Vehículo eliminado", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getSupportFragmentManager(), "DeleteVehicleDialog");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Vehicle vehicle = (Vehicle) data.getSerializableExtra(AddEditVehicleActivity.EXTRA_VEHICLE);

            if (requestCode == REQUEST_ADD_VEHICLE) {
                // Añadir nuevo vehículo
                vehicleList.add(vehicle);
                Toast.makeText(this, "Vehículo añadido", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_EDIT_VEHICLE) {
                // Actualizar vehículo existente
                for (int i = 0; i < vehicleList.size(); i++) {
                    if (vehicleList.get(i).getId().equals(vehicle.getId())) {
                        vehicleList.set(i, vehicle);
                        break;
                    }
                }
                Toast.makeText(this, "Vehículo actualizado", Toast.LENGTH_SHORT).show();
            }

            updateUI();
        }
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
