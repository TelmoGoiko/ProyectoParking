package com.lksnext.parkingplantilla.view.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.FragmentAnadirEditarVehiculoBinding;
import com.lksnext.parkingplantilla.model.Vehicle;
import com.lksnext.parkingplantilla.viewmodel.MainViewModel;
import com.lksnext.parkingplantilla.domain.Callback;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AnadirEditarVehiculoFragment extends Fragment {
    private FragmentAnadirEditarVehiculoBinding binding;
    private MainViewModel viewModel;
    private Vehicle vehicleToEdit;

    private ArrayAdapter<String> marcaAdapter;
    private ArrayAdapter<String> modeloAdapter;
    private ArrayList<String> marcas = new ArrayList<>();
    private ArrayList<String> modelos = new ArrayList<>();
    private String selectedMarca = null;
    private String selectedModelo = null;

    // Variables para cachear los datos de motos
    private ArrayList<Integer> motoBrandIds = new ArrayList<>();
    private JSONArray motoModelsArray = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAnadirEditarVehiculoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Use the shared Toolbar from MainActivity
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(vehicleToEdit == null ? getString(R.string.anadir_editar_vehiculo) : getString(R.string.editar_vehiculo));
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        requireActivity().findViewById(R.id.mainToolbar).setOnClickListener(v -> requireActivity().onBackPressed());
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Spinner de tipo de vehículo
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new String[]{
                getString(R.string.tipo_coche),
                getString(R.string.tipo_moto)
        });
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTipo.setAdapter(tipoAdapter);

        // Adaptadores para los spinners de marca y modelo
        marcaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, marcas);
        marcaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerMarca.setAdapter(marcaAdapter);

        modeloAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, modelos);
        modeloAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerModelo.setAdapter(modeloAdapter);

        binding.spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // Coche
                    binding.layoutElectric.setVisibility(View.VISIBLE);
                    cargarMarcas();
                } else { // Moto
                    binding.layoutElectric.setVisibility(View.GONE);
                    binding.switchElectric.setChecked(false); // Asegurar que no está marcado
                    cargarMotos();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.spinnerMarca.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMarca = marcas.get(position);
                int tipoPos = binding.spinnerTipo.getSelectedItemPosition();
                if (tipoPos == 0) { // Coche
                    cargarModelos(selectedMarca);
                } else { // Moto
                    cargarModelosMoto(position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.spinnerModelo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!modelos.isEmpty()) {
                    selectedModelo = modelos.get(position);
                    // Si el switch eléctrico está activado, cargar trims eléctricos
                    if (binding.switchElectric.isChecked()) {
                        cargarTrimsElectricos(selectedMarca, selectedModelo);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Si recibimos un vehículo para editar, rellenar campos
        if (getArguments() != null && getArguments().containsKey("vehicle")) {
            vehicleToEdit = (Vehicle) getArguments().getSerializable("vehicle");
            if (vehicleToEdit != null) {
                binding.tvTitulo.setText("Editar Vehículo");
                binding.etNombre.setText(vehicleToEdit.getName());
                binding.etMatricula.setText(vehicleToEdit.getLicensePlate());

                // Primero seleccionar el tipo
                binding.spinnerTipo.setSelection(vehicleToEdit.getType().ordinal());

                // Mostrar/ocultar switch eléctrico según tipo
                if (vehicleToEdit.getType() == Vehicle.VehicleType.MOTORCYCLE) {
                    binding.layoutElectric.setVisibility(View.GONE);
                    binding.switchElectric.setChecked(false);
                } else {
                    binding.layoutElectric.setVisibility(View.VISIBLE);
                    binding.switchElectric.setChecked(vehicleToEdit.isElectric());
                }

                // Configurar switch de minusválidos (disponible para todos los tipos de vehículo)
                binding.switchMinusvalidos.setChecked(vehicleToEdit.isForDisabled());
            }
        }

        binding.btnGuardar.setOnClickListener(v -> guardarVehiculo());

        binding.switchElectric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Al cambiar el estado, recargar marcas (y por tanto modelos)
            selectedMarca = null;
            selectedModelo = null;
            marcas.clear();
            modelos.clear();
            marcaAdapter.notifyDataSetChanged();
            modeloAdapter.notifyDataSetChanged();
            cargarMarcas();
        });
    }

    private void cargarMarcas() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            ArrayList<String> result = new ArrayList<>();
            try {
                boolean electrico = binding.switchElectric != null && binding.switchElectric.isChecked();
                if (electrico) {
                    // Obtener todos los modelos eléctricos y extraer las marcas únicas
                    String urlStr = "https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getTrims&fuel_type=Electric";
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    String json = response.toString().replaceFirst("^[^\\(]*\\((.*)\\);$", "$1");
                    JSONObject obj = new JSONObject(json);
                    JSONArray trims = obj.getJSONArray("Trims");
                    java.util.HashSet<String> marcasUnicas = new java.util.HashSet<>();
                    for (int i = 0; i < trims.length(); i++) {
                        JSONObject trim = trims.getJSONObject(i);
                        String makeDisplay = trim.optString("make_display");
                        if (!TextUtils.isEmpty(makeDisplay)) {
                            marcasUnicas.add(makeDisplay);
                        }
                    }
                    result.addAll(marcasUnicas);
                } else {
                    // Usar year=2020 para obtener marcas activas recientes
                    String urlStr = "https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getMakes&year=2020";
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    String json = response.toString().replaceFirst("^[^\\(]*\\((.*)\\);$", "$1");
                    JSONObject obj = new JSONObject(json);
                    JSONArray makes = obj.getJSONArray("Makes");
                    for (int i = 0; i < makes.length(); i++) {
                        JSONObject make = makes.getJSONObject(i);
                        result.add(make.getString("make_display"));
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(getContext(), getString(R.string.error_cargando_marcas, e.getMessage()), Toast.LENGTH_LONG).show());
            }
            handler.post(() -> {
                marcas.clear();
                marcas.addAll(result);
                marcaAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), getString(R.string.marcas_recibidas, result.size()), Toast.LENGTH_LONG).show();
                // Si editando, seleccionar marca
                if (vehicleToEdit != null && vehicleToEdit.getBrand() != null) {
                    int pos = marcas.indexOf(vehicleToEdit.getBrand());
                    if (pos >= 0) binding.spinnerMarca.setSelection(pos);
                }
            });
        });
    }

    // Nueva función auxiliar para comprobar si una marca tiene modelos eléctricos
    private boolean tieneModelosElectricos(String makeId) {
        try {
            String urlStr = "https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getTrims&make=" + makeId + "&fuel_type=Electric";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            String json = response.toString().replaceFirst("^[^\\(]*\\((.*)\\);$", "$1");
            JSONObject obj = new JSONObject(json);
            JSONArray trims = obj.getJSONArray("Trims");
            return trims.length() > 0;
        } catch (IOException | JSONException e) {
            return false;
        }
    }

    private void cargarModelos(String marca) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            ArrayList<String> result = new ArrayList<>();
            try {
                String makeParam = marca.replace(" ", "%20");
                boolean electrico = binding.switchElectric != null && binding.switchElectric.isChecked();
                if (electrico) {
                    String urlStr = "https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getTrims&make=" + makeParam + "&fuel_type=Electric";
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    String json = response.toString().replaceFirst("^[^\\(]*\\((.*)\\);$", "$1");
                    JSONObject obj = new JSONObject(json);
                    JSONArray trims = obj.getJSONArray("Trims");
                    java.util.HashSet<String> modelosUnicos = new java.util.HashSet<>();
                    for (int i = 0; i < trims.length(); i++) {
                        JSONObject trim = trims.getJSONObject(i);
                        String modelName = trim.optString("model_name");
                        if (!TextUtils.isEmpty(modelName)) {
                            modelosUnicos.add(modelName);
                        }
                    }
                    result.addAll(modelosUnicos);
                } else {
                    StringBuilder urlStr = new StringBuilder("https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getModels&make=" + makeParam);
                    URL url = new URL(urlStr.toString());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    String json = response.toString().replaceFirst("^[^\\(]*\\((.*)\\);$", "$1");
                    JSONObject obj = new JSONObject(json);
                    JSONArray models = obj.getJSONArray("Models");
                    for (int i = 0; i < models.length(); i++) {
                        JSONObject model = models.getJSONObject(i);
                        result.add(model.getString("model_name"));
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(getContext(), getString(R.string.error_cargando_modelos, e.getMessage()), Toast.LENGTH_LONG).show());
            }
            handler.post(() -> {
                modelos.clear();
                modelos.addAll(result);
                modeloAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), getString(R.string.modelos_recibidos, result.size()), Toast.LENGTH_LONG).show();
                // Si editando, seleccionar modelo
                if (vehicleToEdit != null && vehicleToEdit.getModel() != null) {
                    int pos = modelos.indexOf(vehicleToEdit.getModel());
                    if (pos >= 0) binding.spinnerModelo.setSelection(pos);
                }
            });
        });
    }

    private void cargarTrimsElectricos(String marca, String modelo) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            ArrayList<String> trimsElectricos = new ArrayList<>();
            try {
                String makeParam = marca.replace(" ", "%20");
                String modelParam = modelo.replace(" ", "%20");
                String urlStr = "https://www.carqueryapi.com/api/0.3/?callback=?&cmd=getTrims&make=" + makeParam + "&model=" + modelParam + "&fuel_type=Electric";
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                String json = response.toString().replaceFirst("^[^\\(]*\\((.*)\\);$", "$1");
                JSONObject obj = new JSONObject(json);
                JSONArray trims = obj.getJSONArray("Trims");
                for (int i = 0; i < trims.length(); i++) {
                    JSONObject trim = trims.getJSONObject(i);
                    String trimName = trim.optString("model_trim");
                    String year = trim.optString("model_year");
                    String display = year + " " + modelo + (trimName.isEmpty() ? "" : (" " + trimName));
                    trimsElectricos.add(display);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(getContext(), getString(R.string.error_cargando_electricos, e.getMessage()), Toast.LENGTH_LONG).show());
            }
            handler.post(() -> {
                if (trimsElectricos.isEmpty()) {
                    Toast.makeText(getContext(), getString(R.string.no_versiones_electricas), Toast.LENGTH_LONG).show();
                } else {
                    // Mostrar trims eléctricos en un diálogo o spinner, según tu UI
                    // Por ejemplo, puedes mostrar un diálogo para que el usuario elija el trim
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.selecciona_version_electrica))
                        .setItems(trimsElectricos.toArray(new String[0]), (dialog, which) -> {
                            // Puedes guardar la selección si lo necesitas
                        })
                        .setNegativeButton(getString(R.string.cancelar), null)
                        .show();
                }
            });
        });
    }

    private void cargarMotos() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            ArrayList<String> marcasMotos = new ArrayList<>();
            motoBrandIds.clear();
            try {
                // Leer marcas de motos desde assets
                InputStream isBrands = requireContext().getAssets().open("motos/moto_brands.json");
                int sizeBrands = isBrands.available();
                byte[] bufferBrands = new byte[sizeBrands];
                isBrands.read(bufferBrands);
                isBrands.close();
                String jsonBrands = new String(bufferBrands, "UTF-8");
                JSONObject brandsObj = new JSONObject(jsonBrands);
                JSONArray brandsArray = brandsObj.getJSONArray("data");
                for (int i = 0; i < brandsArray.length(); i++) {
                    JSONObject brand = brandsArray.getJSONObject(i);
                    marcasMotos.add(brand.getString("name"));
                    motoBrandIds.add(brand.getInt("id"));
                }
                // Leer modelos de motos desde assets y cachear
                InputStream isModels = requireContext().getAssets().open("motos/moto_models.json");
                int sizeModels = isModels.available();
                byte[] bufferModels = new byte[sizeModels];
                isModels.read(bufferModels);
                isModels.close();
                String jsonModels = new String(bufferModels, "UTF-8");
                JSONObject modelsObj = new JSONObject(jsonModels);
                motoModelsArray = modelsObj.getJSONArray("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(getContext(), getString(R.string.error_cargando_motos, e.getMessage()), Toast.LENGTH_LONG).show());
            }
            handler.post(() -> {
                marcas.clear();
                marcas.addAll(marcasMotos);
                marcaAdapter.notifyDataSetChanged();
                modelos.clear();
                modeloAdapter.notifyDataSetChanged();
            });
        });
    }

    private void cargarModelosMoto(int brandPosition) {
        ArrayList<String> modelosMotos = new ArrayList<>();
        if (motoModelsArray != null && brandPosition >= 0 && brandPosition < motoBrandIds.size()) {
            int brandId = motoBrandIds.get(brandPosition);
            for (int i = 0; i < motoModelsArray.length(); i++) {
                try {
                    JSONObject model = motoModelsArray.getJSONObject(i);
                    if (model.getInt("brand_id") == brandId) {
                        modelosMotos.add(model.getString("name"));
                    }
                } catch (JSONException e) { /* Ignorar modelo mal formado */ }
            }
        }
        modelos.clear();
        modelos.addAll(modelosMotos);
        modeloAdapter.notifyDataSetChanged();
    }

    private void guardarVehiculo() {
        String nombre = binding.etNombre.getText().toString().trim();
        String matricula = binding.etMatricula.getText().toString().trim();
        String marca = (String) binding.spinnerMarca.getSelectedItem();
        String modelo = (String) binding.spinnerModelo.getSelectedItem();
        int tipoPos = binding.spinnerTipo.getSelectedItemPosition();
        Vehicle.VehicleType tipo = Vehicle.VehicleType.values()[tipoPos];
        boolean isElectric = binding.switchElectric.isChecked();
        boolean isForDisabled = binding.switchMinusvalidos.isChecked();

        // Validar formato de matrícula española: 4 números y 3 letras mayúsculas sin vocales
        if (!matricula.matches("^[0-9]{4}[B-DF-HJ-NP-TV-Z]{3}$")) {
            Toast.makeText(getContext(), getString(R.string.matricula_no_valida), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(matricula) || TextUtils.isEmpty(marca) || TextUtils.isEmpty(modelo)) {
            Toast.makeText(getContext(), getString(R.string.completa_todos_los_campos), Toast.LENGTH_SHORT).show();
            return;
        }

        String id = (vehicleToEdit != null) ? vehicleToEdit.getId() : UUID.randomUUID().toString();
        Vehicle vehiculo = new Vehicle(id, nombre, matricula, marca, modelo, tipo, isElectric, isForDisabled);

        // Delegar toda la lógica de guardado al ViewModel (que a su vez delega al DataRepository)
        viewModel.addOrUpdateVehicle(vehiculo, vehicleToEdit != null, new Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), getString(R.string.vehiculo_guardado), Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
            @Override
            public void onFailure() {
                Toast.makeText(getContext(), getString(R.string.error_guardar_vehiculo), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
