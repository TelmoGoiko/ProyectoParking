package com.lksnext.parkingplantilla.view.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lksnext.parkingplantilla.R;

public class RecoverPasswordActivity extends AppCompatActivity {
    private EditText emailText;
    private Button btnRecover;
    private TextView backToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);

        emailText = findViewById(R.id.emailText);
        btnRecover = findViewById(R.id.btnRecover);
        backToLogin = findViewById(R.id.backToLogin);

        btnRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RecoverPasswordActivity.this, "Introduce tu correo electrónico", Toast.LENGTH_SHORT).show();
                } else {
                    // Aquí iría la lógica real de recuperación
                    Toast.makeText(RecoverPasswordActivity.this, "Enlace de recuperación enviado (simulado)", Toast.LENGTH_SHORT).show();
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
