package com.prm392g2.prmapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.api.UserApi;
import com.prm392g2.prmapp.dtos.users.LoginRequestDTO;
import com.prm392g2.prmapp.dtos.users.LoginResponseDTO;
import com.prm392g2.prmapp.dtos.users.SendOtpRequestDTO;
import com.prm392g2.prmapp.dtos.users.VerifyOtpRequestDTO;
import com.prm392g2.prmapp.network.ApiClient;
import com.prm392g2.prmapp.services.UsersService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    TextView infoMessageTextView;
    private EditText otpEditText;
    private Button verifyOtpButton;
    private Button resendOtpButton;
    private String email;
    private String username;
    private String password;
    private String purpose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        infoMessageTextView = findViewById(R.id.textViewInfoMessage);
        otpEditText = findViewById(R.id.editTextOtp);
        verifyOtpButton = findViewById(R.id.buttonVerifyOtp);
        resendOtpButton = findViewById(R.id.buttonResendOtp);

        // Get passed data from RegisterActivity
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        purpose = intent.getStringExtra("purpose");

        switch (purpose) {
            case "register":
                infoMessageTextView.setText(getString(R.string.otp_sent_register));
                break;
            case "reset-password":
                infoMessageTextView.setText(getString(R.string.otp_sent_reset_password));
                break;
            case "change-email":
                infoMessageTextView.setText(getString(R.string.otp_sent_change_email));
                break;
            default:
        }

        verifyOtpButton.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            if (otp.isEmpty()) {
                Toast.makeText(this, getString(R.string.otp_empty), Toast.LENGTH_SHORT).show();
                return;
            }

            VerifyOtpRequestDTO verifyRequest = new VerifyOtpRequestDTO(email, otp, purpose);
            switch (purpose) {
                case "register":
                    completeRegistration(verifyRequest);
                    break;
                case "reset-password":
                    confirmResetPassword(verifyRequest);
                    break;
                case "change-email":
                    confirmEmailChange(verifyRequest);
                    break;
                default:
                    Toast.makeText(this, getString(R.string.unknown_purpose), Toast.LENGTH_SHORT).show();
            }
        });

        resendOtpButton.setOnClickListener(v -> {
            resendOtpButton.setEnabled(false);

            SendOtpRequestDTO resendRequest = new SendOtpRequestDTO(email, purpose);
            UserApi api = ApiClient.getInstance().create(UserApi.class);
            api.sendOtp(resendRequest).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(VerifyOtpActivity.this, getString(R.string.otp_resent_success), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(VerifyOtpActivity.this, getString(R.string.otp_resent_failed), Toast.LENGTH_SHORT).show();
                    }

                    new CountDownTimer(15000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            resendOtpButton.setText(getString(R.string.resend_otp_with_seconds, millisUntilFinished / 1000));
                        }

                        public void onFinish() {
                            resendOtpButton.setEnabled(true);
                            resendOtpButton.setText(getString(R.string.resend_otp));
                        }
                    }.start();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(VerifyOtpActivity.this, getString(R.string.network_error_with_message, t.getMessage()), Toast.LENGTH_SHORT).show();
                    resendOtpButton.setEnabled(true);
                }
            });
        });
    }

    private void completeRegistration(VerifyOtpRequestDTO request) {
        verifyOtpButton.setEnabled(false);

        UserApi api = ApiClient.getInstance().create(UserApi.class);
        api.completeRegistration(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                verifyOtpButton.setEnabled(true);
                if (response.isSuccessful()) {
                    // Now perform login after successful registration
                    LoginRequestDTO loginRequest = new LoginRequestDTO(username, password);
                    UsersService.getInstance().login(username, password, new Callback<LoginResponseDTO>() {
                        @Override
                        public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().getToken() != null) {
                                Toast.makeText(VerifyOtpActivity.this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(VerifyOtpActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(VerifyOtpActivity.this, getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponseDTO> call, Throwable t) {
                            Toast.makeText(VerifyOtpActivity.this, getString(R.string.login_unable), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();

                            if (errorBody.contains("expired") || errorBody.contains("invalid OTP")) {
                                Toast.makeText(VerifyOtpActivity.this, getString(R.string.otp_invalid_expired), Toast.LENGTH_LONG).show();
                            } else if (errorBody.contains("session expired")) {
                                Toast.makeText(VerifyOtpActivity.this, getString(R.string.registration_session_expired), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(VerifyOtpActivity.this, getString(R.string.registration_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(VerifyOtpActivity.this, getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(VerifyOtpActivity.this, getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                verifyOtpButton.setEnabled(true);
                Toast.makeText(VerifyOtpActivity.this, getString(R.string.network_error_retry), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmResetPassword(VerifyOtpRequestDTO request) {
        verifyOtpButton.setEnabled(false);

        UserApi api = ApiClient.getInstance().create(UserApi.class);
        api.confirmResetPassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                verifyOtpButton.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(VerifyOtpActivity.this, getString(R.string.otp_verified_reset_password), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(VerifyOtpActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("email", request.getEmail());
                    intent.putExtra("otp", request.getOtp());
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();

                            if (errorBody.contains("expired") || errorBody.contains("invalid")) {
                                Toast.makeText(VerifyOtpActivity.this, getString(R.string.otp_invalid_expired), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(VerifyOtpActivity.this, getString(R.string.otp_verification_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(VerifyOtpActivity.this, getString(R.string.unknown_server_error_otp), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(VerifyOtpActivity.this, getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                verifyOtpButton.setEnabled(true);
                Toast.makeText(VerifyOtpActivity.this, getString(R.string.network_error_try_again), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmEmailChange(VerifyOtpRequestDTO request) {
        verifyOtpButton.setEnabled(false);

        String token = getSharedPreferences("auth", MODE_PRIVATE).getString("token", null);
        if (token == null) {
            Toast.makeText(this, getString(R.string.not_logged_in), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        UserApi api = ApiClient.getInstance().create(UserApi.class);
        api.confirmEmailChange("Bearer " + token, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                verifyOtpButton.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(VerifyOtpActivity.this, getString(R.string.email_changed_success), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : getString(R.string.unknown_error);
                        Toast.makeText(VerifyOtpActivity.this, getString(R.string.failed_change_email, error), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(VerifyOtpActivity.this, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                verifyOtpButton.setEnabled(true);
                Toast.makeText(VerifyOtpActivity.this, getString(R.string.network_error_with_message, t.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }
}