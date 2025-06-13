package com.lksnext.parkingplantilla.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.lksnext.parkingplantilla.databinding.DialogDeleteVehicleBinding;
import com.lksnext.parkingplantilla.model.Vehicle;

public class DeleteVehicleDialogFragment extends DialogFragment {

    private static final String ARG_VEHICLE = "vehicle";

    private DialogDeleteVehicleBinding binding;
    private Vehicle vehicle;
    private DeleteVehicleListener listener;

    public interface DeleteVehicleListener {
        void onDeleteConfirmed(Vehicle vehicle);
    }

    public static DeleteVehicleDialogFragment newInstance(Vehicle vehicle) {
        DeleteVehicleDialogFragment fragment = new DeleteVehicleDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_VEHICLE, vehicle);
        fragment.setArguments(args);
        return fragment;
    }

    public void setDeleteVehicleListener(DeleteVehicleListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vehicle = (Vehicle) getArguments().getSerializable(ARG_VEHICLE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogDeleteVehicleBinding.inflate(LayoutInflater.from(getContext()));

        // Personalizar mensaje si tenemos datos del vehículo
        if (vehicle != null) {
            String message = "¿Estás seguro de que quieres eliminar el vehículo \"" +
                    vehicle.getName() + "\" (" + vehicle.getLicensePlate() + ")?";
            binding.tvDeleteVehicleMessage.setText(message);
        }

        // Configurar botones
        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnConfirmDelete.setOnClickListener(v -> {
            if (listener != null && vehicle != null) {
                listener.onDeleteConfirmed(vehicle);
            }
            dismiss();
        });

        return new AlertDialog.Builder(requireContext())
                .setView(binding.getRoot())
                .create();
    }
}
