package com.prm392g2.prmapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.prm392g2.prmapp.R;

public class PasswordResetSuccessActivity extends AppCompatActivity {

    private Button backToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset_success);

        backToLoginButton = findViewById(R.id.buttonBackToLogin);

        backToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(PasswordResetSuccessActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}