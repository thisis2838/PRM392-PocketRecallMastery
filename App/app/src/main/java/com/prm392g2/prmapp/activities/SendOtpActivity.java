package com.prm392g2.prmapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.api.UserApi;
import com.prm392g2.prmapp.dtos.users.SendOtpRequestDTO;
import com.prm392g2.prmapp.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendOtpActivity extends AppCompatActivity {

    private EditText emailEditText, purposeEditText;
    private Button sendOtpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_otp);

        emailEditText = findViewById(R.id.editTextEmail);
        purposeEditText = findViewById(R.id.editTextPurpose);
        sendOtpButton = findViewById(R.id.buttonSendOtp);

        sendOtpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String purpose = purposeEditText.getText().toString().trim();

            SendOtpRequestDTO request = new SendOtpRequestDTO(email, purpose);
            UserApi api = ApiClient.getClient().create(UserApi.class);
            api.sendOtp(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(SendOtpActivity.this, "✅ OTP sent successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        switch (response.code()) {
                            case 400:
                                Toast.makeText(SendOtpActivity.this, "❌ Email and purpose are required or invalid request.", Toast.LENGTH_LONG).show();
                                break;
                            case 404:
                                Toast.makeText(SendOtpActivity.this, "❌ User not found.", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(SendOtpActivity.this, "❌ Failed to send OTP. Code: " + response.code(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(SendOtpActivity.this, "⚠️ Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
