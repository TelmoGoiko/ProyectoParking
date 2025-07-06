package com.lksnext.parkingplantilla.view.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.lksnext.parkingplantilla.R;
import com.lksnext.parkingplantilla.viewmodel.RecoverPasswordViewModel;

public class RecoverPasswordActivity extends AppCompatActivity {
    private EditText emailText;
    private Button btnRecover;
    private TextView backToLogin;

    private RecoverPasswordViewModel recoverPasswordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        emailText = findViewById(R.id.emailText);
        btnRecover = findViewById(R.id.btnRecover);
        backToLogin = findViewById(R.id.backToLogin);

        recoverPasswordViewModel = new ViewModelProvider(this).get(RecoverPasswordViewModel.class);

        recoverPasswordViewModel.getSuccess().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean success) {
                if (success != null && success) {
                    Toast.makeText(RecoverPasswordActivity.this, getString(R.string.enlace_recuperacion_enviado), Toast.LENGTH_SHORT).show();
                }
            }
        });
        recoverPasswordViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null) {
                    Toast.makeText(RecoverPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RecoverPasswordActivity.this, getString(R.string.introduce_correo_electronico), Toast.LENGTH_SHORT).show();
                } else {
                    // Aquí iría la lógica real de recuperación
                    recoverPasswordViewModel.sendPasswordResetEmail(email);
                }
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Vuelve a la pantalla anterior (login)
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(updateLocale(newBase));
    }

    private static Context updateLocale(Context context) {
        android.content.SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String langCode = prefs.getString("app_language", java.util.Locale.getDefault().getLanguage());
        java.util.Locale locale;
        if (langCode.contains("-")) {
            String[] parts = langCode.split("-");
            locale = new java.util.Locale.Builder().setLanguage(parts[0]).setRegion(parts[1]).build();
        } else {
            locale = new java.util.Locale.Builder().setLanguage(langCode).build();
        }
        java.util.Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = res.getConfiguration();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
            return context;
        }
    }
}
