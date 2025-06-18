package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.FragmentAparcarYaBinding;
import java.util.Calendar;

public class AparcarYaFragment extends Fragment {
    private FragmentAparcarYaBinding binding;
    private Calendar fechaSeleccionada = Calendar.getInstance();
    private Calendar horaSeleccionada = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAparcarYaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar el DatePicker con la fecha actual
        DatePicker datePicker = binding.datePicker;
        datePicker.init(
            fechaSeleccionada.get(Calendar.YEAR),
            fechaSeleccionada.get(Calendar.MONTH),
            fechaSeleccionada.get(Calendar.DAY_OF_MONTH),
            (view1, year, monthOfYear, dayOfMonth) -> {
                fechaSeleccionada.set(Calendar.YEAR, year);
                fechaSeleccionada.set(Calendar.MONTH, monthOfYear);
                fechaSeleccionada.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        );

        // Configurar el TimePicker con la hora actual
        TimePicker timePicker = binding.timePicker;
        timePicker.setIs24HourView(true);
        timePicker.setHour(horaSeleccionada.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(horaSeleccionada.get(Calendar.MINUTE));
        timePicker.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
            horaSeleccionada.set(Calendar.HOUR_OF_DAY, hourOfDay);
            horaSeleccionada.set(Calendar.MINUTE, minute);
        });

        // Configurar el botón de búsqueda
        binding.btnAparcar.setOnClickListener(v -> {
            String fecha = String.format("%02d/%02d/%04d",
                fechaSeleccionada.get(Calendar.DAY_OF_MONTH),
                fechaSeleccionada.get(Calendar.MONTH) + 1,
                fechaSeleccionada.get(Calendar.YEAR));

            String hora = String.format("%02d:%02d",
                horaSeleccionada.get(Calendar.HOUR_OF_DAY),
                horaSeleccionada.get(Calendar.MINUTE));

            Toast.makeText(getContext(), "Aparcando el " + fecha + " a las " + hora, Toast.LENGTH_SHORT).show();

            // Navegar al fragmento de selección de vehículo
            Navigation.findNavController(view).navigate(R.id.action_aparcarYaFragment_to_seleccionVehiculoFragment);
        });

        // Configurar el botón de navegación hacia atrás
        binding.toolbarAparcar.setNavigationOnClickListener(v -> {
            requireActivity().onBackPressed();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
