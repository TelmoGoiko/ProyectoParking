package com.lksnext.parkingplantilla.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private List<Vehicle> vehicles = new ArrayList<>();
    private OnVehicleClickListener listener;

    public interface OnVehicleClickListener {
        void onVehicleClick(Vehicle vehicle);
        void onEditClick(Vehicle vehicle);
        void onDeleteClick(Vehicle vehicle);
    }

    public VehicleAdapter(OnVehicleClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehiculo, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicles.get(position);
        holder.bind(vehicle);
    }

    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
        notifyDataSetChanged();
    }

    public void updateVehicleList(List<Vehicle> newVehicles) {
        this.vehicles.clear();
        this.vehicles.addAll(newVehicles);
        notifyDataSetChanged();
    }

    class VehicleViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVehiculoNombre;
        private TextView tvMatricula;
        private TextView tvMarcaModelo;
        private TextView tvTipoVehiculo;
        private TextView tvElectric;
        private ImageButton btnEditarVehiculo;
        private ImageButton btnBorrarVehiculo;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVehiculoNombre = itemView.findViewById(R.id.tvVehiculoNombre);
            tvMatricula = itemView.findViewById(R.id.tvMatricula);
            tvMarcaModelo = itemView.findViewById(R.id.tvMarcaModelo);
            tvTipoVehiculo = itemView.findViewById(R.id.tvTipoVehiculo);
            tvElectric = itemView.findViewById(R.id.tvElectric);
            btnEditarVehiculo = itemView.findViewById(R.id.btnEditarVehiculo);
            btnBorrarVehiculo = itemView.findViewById(R.id.btnBorrarVehiculo);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onVehicleClick(vehicles.get(position));
                }
            });

            btnEditarVehiculo.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEditClick(vehicles.get(position));
                }
            });

            btnBorrarVehiculo.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDeleteClick(vehicles.get(position));
                }
            });
        }

        public void bind(Vehicle vehicle) {
            tvVehiculoNombre.setText(vehicle.getName());
            tvMatricula.setText(vehicle.getLicensePlate());
            tvMarcaModelo.setText(vehicle.getBrand() + " " + vehicle.getModel());
            tvTipoVehiculo.setText(vehicle.getType().getDisplayName());
            if (vehicle.isElectric()) {
                tvElectric.setVisibility(View.VISIBLE);
            } else {
                tvElectric.setVisibility(View.GONE);
            }
        }
    }
}
