package com.prm392g2.prmapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.api.UserApi;
import com.prm392g2.prmapp.dtos.users.RegisterDTO;
import com.prm392g2.prmapp.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity
{

    EditText editUsername, editEmail, editPassword, editConfirmPassword;
    Button btnRegister, btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        btnRegister.setOnClickListener(v ->
        {
            String username = editUsername.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString();
            String confirmPassword = editConfirmPassword.getText().toString();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
            {
                Toast.makeText(this, getString(R.string.register_all_fields_required), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidEmail(email))
            {
                Toast.makeText(this, getString(R.string.register_invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword))
            {
                Toast.makeText(this, getString(R.string.register_passwords_no_match), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(password))
            {
                Toast.makeText(this, getString(R.string.register_password_requirements), Toast.LENGTH_LONG).show();
                return;
            }

            RegisterDTO dto = new RegisterDTO(username, email, password);

            UserApi userApi = ApiClient.getInstance().getUserApi();
            btnRegister.setEnabled(false);
            userApi.preRegister(dto).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    btnRegister.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, getString(R.string.register_otp_sent), Toast.LENGTH_SHORT).show();

                        // Go to OTP activity
                        Intent intent = new Intent(RegisterActivity.this, VerifyOtpActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        intent.putExtra("username", username);
                        intent.putExtra("purpose", "register");
                        startActivity(intent);
                    } else {
                        String errorMessage = getString(R.string.register_failed_with_code, String.valueOf(response.code()));
                        try {
                            if (response.errorBody() != null) {
                                errorMessage = response.errorBody().string();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    btnRegister.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, getString(R.string.register_error_with_message, t.getMessage()), Toast.LENGTH_SHORT).show();                }
            });
        });

        btnBackToLogin.setOnClickListener(v ->
        {
            finish();
        });
    }

    private boolean isValidEmail(String email)
    {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password)
    {
        // At least 6 characters, 1 upper, 1 lower, 1 digit, 1 special character
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\d]).{6,}$";
        return password.matches(passwordPattern);
    }
}