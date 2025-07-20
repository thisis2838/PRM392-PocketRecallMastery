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
import android.widget.EditText;
import android.widget.TextView;

import com.prm392g2.prmapp.api.UserApi;
import com.prm392g2.prmapp.dtos.users.UserDto;
import com.prm392g2.prmapp.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.activities.LoginActivity;

public class ProfileFragment extends Fragment {

    private TextView tvUserInfo;
    private EditText etJwt;
    private Button btnSettings, btnPersonalInfo, btnLogin;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUserInfo = view.findViewById(R.id.tvUserInfo);
        etJwt = view.findViewById(R.id.etJwt);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnPersonalInfo = view.findViewById(R.id.btnPersonalInfo);
        btnLogin = view.findViewById(R.id.btnLogin);

        loadJwt();
        loadUserInfo();
        updateButtonVisibility();

        btnSettings.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .addToBackStack(null)
                .commit());

        btnPersonalInfo.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new PersonalInfoFragment())
                .addToBackStack(null)
                .commit());

        btnLogin.setOnClickListener(v -> {
            // Redirect to LoginActivity or your login flow
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadJwt() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);
        if (token == null || token.isEmpty()) {
            etJwt.setText("Not logged in");
        } else {
            etJwt.setText(token);
        }
    }

    private void updateButtonVisibility() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        boolean loggedIn = token != null && !token.isEmpty();

        btnSettings.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        btnPersonalInfo.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
        btnLogin.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
    }

    private void loadUserInfo() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", null);

        if (token == null || token.isEmpty()) {
            tvUserInfo.setText("User not logged in");
            return;
        }

        UserApi api = ApiClient.getClient().create(UserApi.class);
        Call<UserDto> call = api.getCurrentUser("Bearer " + token);

        call.enqueue(new Callback<UserDto>() {
            @Override
            public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDto user = response.body();

                    String info = "ID: " + user.id + "\n"
                            + "Username: " + user.username + "\n"
                            + "Email: " + user.email + "\n";

                    tvUserInfo.setText(info);
                } else {
                    tvUserInfo.setText("Failed to load user info: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserDto> call, Throwable t) {
                tvUserInfo.setText("Error loading user info: " + t.getMessage());
            }
        });
    }

}
