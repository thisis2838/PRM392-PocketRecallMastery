package com.prm392g2.prmapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.activities.ChangePasswordActivity;
import com.prm392g2.prmapp.activities.LoginActivity;
import com.prm392g2.prmapp.activities.MainActivity;
import com.prm392g2.prmapp.api.UserApi;
import com.prm392g2.prmapp.dialogs.EmailChangeDialog;
import com.prm392g2.prmapp.dtos.users.UserSummaryDTO;
import com.prm392g2.prmapp.network.ApiClient;
import com.prm392g2.prmapp.services.UsersService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInfoFragment extends Fragment {

    private TextView tvUsername, tvEmail;
    private Button btnEditEmail, btnLogout, btnChangePassword;

    public PersonalInfoFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        btnEditEmail = view.findViewById(R.id.btnEditEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);

        loadUserInfo();

        btnEditEmail.setOnClickListener(v -> {
            new EmailChangeDialog(newEmail -> {
                // Call API to request email change with OTP
                UsersService.getInstance().requestEmailChange(newEmail, new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), getString(R.string.otp_sent_new_email), Toast.LENGTH_SHORT).show();
                            // Navigate to OTP dialog/activity
                        } else if (response.code() == 400) {
                            try {
                                String error = response.errorBody().string();
                                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getContext(), getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                });
            }).show(getParentFragmentManager(), "ChangeEmailDialog");
        });

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
            prefs.edit().remove("token").apply();
            prefs.edit().remove("userId").apply();

            Toast.makeText(requireContext(), getString(R.string.logged_out), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    private void loadUserInfo() {
        UserApi api = ApiClient.getInstance().create(UserApi.class);
        api.getCurrentUser().enqueue(new Callback<UserSummaryDTO>() {
            @Override
            public void onResponse(Call<UserSummaryDTO> call, Response<UserSummaryDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvUsername.setText(response.body().username);
                    tvEmail.setText(response.body().email);
                } else {
                    Toast.makeText(requireContext(), getString(R.string.failed_load_user_info), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserSummaryDTO> call, Throwable t) {
                Toast.makeText(requireContext(), getString(R.string.error_with_message, t.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
        ((MainActivity) requireActivity()).setToolbarTitle(getString(R.string.personal_info_title));    }
}
