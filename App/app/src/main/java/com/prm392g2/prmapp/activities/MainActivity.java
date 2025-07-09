package com.prm392g2.prmapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.prm392g2.prmapp.R;

public class MainActivity extends AppCompatActivity
{

    private Button btnLoginLogout, btnLoginInfo;
    private SharedPreferences prefs;
    private static final String PREF_NAME = "auth_pref";
    private static final String KEY_TOKEN = "jwt_token";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLoginLogout = findViewById(R.id.btnLoginLogout);
        btnLoginInfo = findViewById(R.id.btnLoginInfo);
        prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        updateLoginButton();

        btnLoginLogout.setOnClickListener(v -> {
            String token = prefs.getString(KEY_TOKEN, null);
            if (token == null) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                prefs.edit().remove(KEY_TOKEN).apply();
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                updateLoginButton();
            }
        });

        btnLoginInfo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginInfoActivity.class);
            startActivity(intent);
        });
    }

    private void updateLoginButton() {
        String token = prefs.getString(KEY_TOKEN, null);
        if (token == null) {
            btnLoginLogout.setText("Login");
        } else {
            btnLoginLogout.setText("Logout");
        }
    }
}