package com.lksnext.parkingplantilla.view.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.databinding.FragmentUserBinding;
import com.lksnext.parkingplantilla.view.activity.LoginActivity;
import com.lksnext.parkingplantilla.viewmodel.ProfileViewModel;
import java.util.Locale;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.ContextWrapper;

public class UserFragment extends Fragment {
    private FragmentUserBinding binding;
    private ProfileViewModel profileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(updateLocale(context));
    }

    private Context updateLocale(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String langCode = prefs.getString("app_language", Locale.getDefault().getLanguage());
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
            return new ContextWrapper(context);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Use the shared Toolbar from MainActivity
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle(getString(R.string.perfil_usuario));
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        requireActivity().findViewById(com.lksnext.parkingplantilla.R.id.mainToolbar).setOnClickListener(v ->
            requireActivity().getOnBackPressedDispatcher().onBackPressed());

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.loadUserData();

        // Mostrar email
        profileViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
            if (email != null) binding.textViewUserEmail.setText(email);
        });

        // Mostrar nombre de usuario
        profileViewModel.getUserName().observe(getViewLifecycleOwner(), username -> {
            if (username != null) binding.textViewUserName.setText(username);
        });

        // Botón cambiar contraseña
        binding.btnChangePassword.setOnClickListener(v -> {
            String email = binding.textViewUserEmail.getText().toString();
            profileViewModel.sendPasswordResetEmail(email);
        });

        profileViewModel.getPasswordResetResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
        });

        // Botón de logout
        binding.btnLogout.setOnClickListener(v -> {
            profileViewModel.signOut();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Language selector setup
        Spinner spinner = binding.spinnerLanguage;
        String[] languages = {"Español", "English", "Euskera", "Français"};
        String[] languageCodes = {"es", "en", "eu", "fr"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        // Set current language as selected
        String currentLang = Locale.getDefault().getLanguage();
        int selectedIndex = 0;
        for (int i = 0; i < languageCodes.length; i++) {
            if (languageCodes[i].equals(currentLang)) {
                selectedIndex = i;
                break;
            }
        }
        spinner.setSelection(selectedIndex);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String langCode = languageCodes[position];
                if (!Locale.getDefault().getLanguage().equals(langCode)) {
                    saveLanguagePreference(langCode);
                    requireActivity().recreate();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void saveLanguagePreference(String langCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        prefs.edit().putString("app_language", langCode).apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
