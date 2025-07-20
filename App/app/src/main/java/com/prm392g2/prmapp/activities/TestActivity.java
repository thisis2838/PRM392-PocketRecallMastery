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

public class TestActivity extends AppCompatActivity {

    private Button btnSendOtp, btnVerifyOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btnSendOtp = findViewById(R.id.buttonSendOtpActivity);
        btnVerifyOtp = findViewById(R.id.buttonVerifyOtpActivity);

        btnSendOtp.setOnClickListener(v -> {
            Intent intent = new Intent(TestActivity.this, SendOtpActivity.class);
            startActivity(intent);
        });

        btnVerifyOtp.setOnClickListener(v -> {
            Intent intent = new Intent(TestActivity.this, VerifyOtpActivity.class);
            startActivity(intent);
        });
    }

}