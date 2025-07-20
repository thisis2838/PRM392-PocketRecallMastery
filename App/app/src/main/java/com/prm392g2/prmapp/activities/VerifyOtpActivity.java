package com.prm392g2.prmapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.api.UserApi;
import com.prm392g2.prmapp.dtos.users.CompleteRegistrationRequest;
import com.prm392g2.prmapp.dtos.users.LoginRequest;
import com.prm392g2.prmapp.dtos.users.LoginResponse;
import com.prm392g2.prmapp.dtos.users.RegisterDto;
import com.prm392g2.prmapp.dtos.users.SendOtpRequestDTO;
import com.prm392g2.prmapp.dtos.users.VerifyOtpRequestDTO;
import com.prm392g2.prmapp.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    private EditText otpEditText;
    private Button verifyOtpButton;
    private Button resendOtpButton;
    private String email;
    private String username;
    private String password;
    private String purpose;

    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_TOKEN = "jwt_token";

    private static final String TAG = "VerifyOtpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        otpEditText = findViewById(R.id.editTextOtp);
        verifyOtpButton = findViewById(R.id.buttonVerifyOtp);
        resendOtpButton = findViewById(R.id.buttonResendOtp);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Get passed data from RegisterActivity
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        purpose = intent.getStringExtra("purpose");

        verifyOtpButton.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            if (otp.isEmpty()) {
                Toast.makeText(this, "OTP cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            VerifyOtpRequestDTO verifyRequest = new VerifyOtpRequestDTO(email, otp, purpose);
            switch (purpose) {
                case "register":
                    completeRegistration(verifyRequest);
                    break;
                case "forgot-password":
                    verifyForgotPassword(verifyRequest);
                    break;
                default:
                    Toast.makeText(this, "Unknown purpose.", Toast.LENGTH_SHORT).show();
            }
        });

        resendOtpButton.setOnClickListener(v -> {
            resendOtpButton.setEnabled(false);

            SendOtpRequestDTO resendRequest = new SendOtpRequestDTO(email, purpose);
            UserApi api = ApiClient.getClient().create(UserApi.class);
            api.sendOtp(resendRequest).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(VerifyOtpActivity.this, "OTP resent successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(VerifyOtpActivity.this, "Failed to resend OTP", Toast.LENGTH_SHORT).show();
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                Log.e(TAG, "Resend OTP error body: " + errorBody);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading errorBody", e);
                        }
                    }

                    resendOtpButton.postDelayed(() -> resendOtpButton.setEnabled(true), 1000);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(VerifyOtpActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    resendOtpButton.setEnabled(true);
                    Log.e(TAG, "Resend OTP failed", t);
                }
            });
        });
    }

    private void completeRegistration(VerifyOtpRequestDTO request) {
        verifyOtpButton.setEnabled(false);

        UserApi api = ApiClient.getClient().create(UserApi.class);
        api.completeRegistration(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                verifyOtpButton.setEnabled(true);
                if (response.isSuccessful()) {
                    // Now perform login after successful registration
                    LoginRequest loginRequest = new LoginRequest(username, password);
                    api.login(loginRequest).enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                String token = response.body().getToken();
                                sharedPreferences.edit().putString(KEY_TOKEN, token).apply();

                                Toast.makeText(VerifyOtpActivity.this, "Registration successful! Logging you in...", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(VerifyOtpActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e(TAG, "Login failed after registration: " + response.code());
                                Toast.makeText(VerifyOtpActivity.this, "Login failed. Please try again or log in manually.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            Log.e(TAG, "Login failed after registration", t);
                            Toast.makeText(VerifyOtpActivity.this, "Unable to log in at the moment. Check your connection and try again.", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Complete registration error: " + errorBody);

                            if (errorBody.contains("expired") || errorBody.contains("invalid OTP")) {
                                Toast.makeText(VerifyOtpActivity.this, "The OTP is invalid or has expired. Please try again.", Toast.LENGTH_LONG).show();
                            } else if (errorBody.contains("session expired")) {
                                Toast.makeText(VerifyOtpActivity.this, "Your registration session has expired. Please register again.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(VerifyOtpActivity.this, "Could not complete registration. Please try again.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(VerifyOtpActivity.this, "An unknown error occurred. Please try again later.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading errorBody", e);
                        Toast.makeText(VerifyOtpActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                verifyOtpButton.setEnabled(true);
                Log.e(TAG, "Complete registration failed", t);
                Toast.makeText(VerifyOtpActivity.this, "Network error. Please check your connection and try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void verifyForgotPassword(VerifyOtpRequestDTO request) {
        // Hit the "verify-reset-otp" endpoint,
        // or navigate to a ResetPasswordActivity after verification
        // depending on your server design.
    }
}