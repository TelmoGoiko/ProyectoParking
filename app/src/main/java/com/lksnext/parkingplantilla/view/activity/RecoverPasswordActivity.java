package com.lksnext.parkingplantilla.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;

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
                    Toast.makeText(RecoverPasswordActivity.this, "Enlace de recuperación enviado", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RecoverPasswordActivity.this, "Introduce tu correo electrónico", Toast.LENGTH_SHORT).show();
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
}
