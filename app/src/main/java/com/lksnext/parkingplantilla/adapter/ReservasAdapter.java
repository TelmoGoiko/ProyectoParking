package com.lksnext.parkingplantilla.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lksnext.parkingplantilla.databinding.ItemReservaBinding;
import com.lksnext.parkingplantilla.model.Reserva;

import java.util.List;

public class ReservasAdapter extends RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder> {

    private List<Reserva> reservas;
    private final OnReservaEditClickListener editListener;
    private final OnReservaDeleteClickListener deleteListener;

    public interface OnReservaEditClickListener {
        void onEditClick(Reserva reserva);
    }

    public interface OnReservaDeleteClickListener {
        boolean onDeleteClick(Reserva reserva);
    }

    public ReservasAdapter(List<Reserva> reservas, OnReservaEditClickListener editListener, OnReservaDeleteClickListener deleteListener) {
        this.reservas = reservas;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReservaBinding binding = ItemReservaBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ReservaViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Reserva reserva = reservas.get(position);
        holder.bind(reserva, editListener, deleteListener);
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void actualizarReservas(List<Reserva> nuevasReservas) {
        this.reservas = nuevasReservas;
        notifyDataSetChanged();
    }

    static class ReservaViewHolder extends RecyclerView.ViewHolder {
        private final ItemReservaBinding binding;

        public ReservaViewHolder(ItemReservaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Reserva reserva, OnReservaEditClickListener editListener, OnReservaDeleteClickListener deleteListener) {
            binding.tvNombreParking.setText(reserva.getNombreParking());
            binding.tvFecha.setText(reserva.getFecha());
            binding.tvHorario.setText(String.format("%s - %s", reserva.getHoraInicio(), reserva.getHoraFin()));
            binding.tvEstado.setText(reserva.getEstado());

            // Mostrar el botón de editar solo si la reserva está en estado 'Confirmada'
            if (reserva.getEstado() != null && reserva.getEstado().equalsIgnoreCase("Confirmada")) {
                binding.btnEditar.setVisibility(android.view.View.VISIBLE);
                binding.btnEditar.setEnabled(true);
                binding.btnEditar.setAlpha(1f);
                binding.btnEditar.setOnClickListener(v -> editListener.onEditClick(reserva));
            } else {
                binding.btnEditar.setVisibility(android.view.View.GONE);
                binding.btnEditar.setEnabled(false);
            }
            binding.btnEliminar.setOnClickListener(v -> deleteListener.onDeleteClick(reserva));
        }
    }
}
