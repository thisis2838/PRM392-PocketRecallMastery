package com.prm392g2.prmapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.api.UserApi;
import com.prm392g2.prmapp.dtos.users.UserDto;
import com.prm392g2.prmapp.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginInfoActivity extends AppCompatActivity {

    private TextView loginInfoTextView;
    private EditText tokenEditText;
    private Button btnGoToHome;
    private static final String PREF_NAME = "auth_pref";
    private static final String KEY_TOKEN = "jwt_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_info);

        loginInfoTextView = findViewById(R.id.loginInfoTextView);
        tokenEditText = findViewById(R.id.tokenEditText);
        btnGoToHome = findViewById(R.id.btnGoToHome);

        btnGoToHome.setOnClickListener(v -> {
            Intent intent = new Intent(LoginInfoActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Display token
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(KEY_TOKEN, null);
        tokenEditText.setText(token != null ? token : "No token saved.");

        if (token == null) {
            loginInfoTextView.setText("No login token found.");
            return;
        }

        // Call backend to get user info
        UserApi api = ApiClient.getClient().create(UserApi.class);
        Call<UserDto> call = api.getCurrentUser("Bearer " + token);

        call.enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDto user = response.body();

                    String info = "ID: " + user.id + "\n"
                            + "Username: " + user.username + "\n"
                            + "Email: " + user.email + "\n"
                            + "Language: " + user.language + "\n"
                            + "Theme: " + user.themeName + "\n"
                            + "Notifications: " + user.isNotificationOn;

                    loginInfoTextView.setText(info);
                } else {
                    loginInfoTextView.setText("Failed to load user info: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                loginInfoTextView.setText("Error: " + t.getMessage());
            }
        });
    }
}