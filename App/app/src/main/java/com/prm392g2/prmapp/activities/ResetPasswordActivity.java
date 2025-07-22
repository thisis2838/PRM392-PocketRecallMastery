package com.prm392g2.prmapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.api.UserApi;
import com.prm392g2.prmapp.dtos.users.ResetPasswordDTO;
import com.prm392g2.prmapp.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordEditText, confirmPasswordEditText;
    private Button resetPasswordButton;
    private String email, otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPasswordEditText = findViewById(R.id.editTextNewPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        resetPasswordButton = findViewById(R.id.buttonResetPassword);

        email = getIntent().getStringExtra("email");
        otp = getIntent().getStringExtra("otp");

        resetPasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, getString(R.string.reset_password_no_match), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(newPassword)) {
                Toast.makeText(this, getString(R.string.reset_password_weak), Toast.LENGTH_LONG).show();
                return;
            }

            ResetPasswordDTO dto = new ResetPasswordDTO(email, newPassword);
            UserApi api = ApiClient.getInstance().create(UserApi.class);
            api.requestResetPassword(dto).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(ResetPasswordActivity.this, getString(R.string.reset_password_success), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ResetPasswordActivity.this, PasswordResetSuccessActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, getString(R.string.reset_password_failed), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();                }
            });
        });
    }

    private boolean isValidPassword(String password) {
        // At least 6 characters, 1 upper, 1 lower, 1 digit, 1 special character
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\d]).{6,}$";
        return password.matches(passwordPattern);
    }
}