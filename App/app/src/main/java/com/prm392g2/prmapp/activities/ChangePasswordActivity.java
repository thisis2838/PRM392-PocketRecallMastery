package com.prm392g2.prmapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.api.UserApi;
import com.prm392g2.prmapp.dtos.users.ChangePasswordDTO;
import com.prm392g2.prmapp.network.ApiClient;
import com.prm392g2.prmapp.services.UsersService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity
{

    private EditText editTextCurrentPassword, editTextNewPassword, editTextConfirmNewPassword;
    private Button buttonChangePassword;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmNewPassword = findViewById(R.id.editTextConfirmNewPassword);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v ->
        {
            finish();
        });

        buttonChangePassword.setOnClickListener(v ->
        {
            String currentPassword = editTextCurrentPassword.getText().toString().trim();
            String newPassword = editTextNewPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmNewPassword.getText().toString().trim();

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty())
            {
                Toast.makeText(this, getString(R.string.change_password_fill_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword))
            {
                editTextConfirmNewPassword.setError(getString(R.string.change_password_no_match));
                return;
            }

            if (!isValidPassword(newPassword))
            {
                editTextNewPassword.setError(getString(R.string.change_password_weak));
                return;
            }

            changePassword(currentPassword, newPassword);
        });
    }

    private boolean isValidPassword(String password)
    {
        // At least 6 characters, 1 upper, 1 lower, 1 digit, 1 special character
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\d]).{6,}$";
        return password.matches(passwordPattern);
    }

    private void changePassword(String currentPassword, String newPassword)
    {
        if (!UsersService.getInstance().isLoggedIn())
        {
            Toast.makeText(this, getString(R.string.not_logged_in), Toast.LENGTH_SHORT).show();
            return;
        }

        UserApi api = ApiClient.getInstance().getUserApi();
        ChangePasswordDTO dto = new ChangePasswordDTO(currentPassword, newPassword);

        api.changePassword(dto).enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                if (response.isSuccessful())
                {
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.change_password_success), Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.change_password_failed), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t)
            {
                Toast.makeText(ChangePasswordActivity.this, getString(R.string.network_error_with_message, t.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
