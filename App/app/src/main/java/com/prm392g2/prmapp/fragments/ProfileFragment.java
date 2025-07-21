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
import android.widget.ImageView;
import android.widget.TextView;

import com.prm392g2.prmapp.api.UserApi;
import com.prm392g2.prmapp.dtos.users.UserSummaryDTO;
import com.prm392g2.prmapp.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.activities.LoginActivity;

import java.util.Random;

public class ProfileFragment extends Fragment {

    private TextView tvGreeting;
    private ImageView ivAndroidLogo;
    private Button btnSettings, btnPersonalInfo, btnLogin;

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvGreeting = view.findViewById(R.id.tvGreeting);
        ivAndroidLogo = view.findViewById(R.id.ivAndroidLogo);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnPersonalInfo = view.findViewById(R.id.btnPersonalInfo);
        btnLogin = view.findViewById(R.id.btnLogin);

        btnSettings.setOnClickListener(v -> requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SettingsFragment())
                .addToBackStack(null)
                .commit());

        btnPersonalInfo.setOnClickListener(v -> requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new PersonalInfoFragment())
                .addToBackStack(null)
                .commit());

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        updateUI();

        return view;
    }

    private void updateUI() {
        SharedPreferences prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null || token.isEmpty()) {
            showLoggedOutUI();
        } else {
            loadUserInfo();
        }
    }

    private void showLoggedOutUI() {
        tvGreeting.setText(getString(R.string.greeting_logged_out));
        ivAndroidLogo.setImageResource(R.drawable.ic_expressionless);

        btnLogin.setVisibility(View.VISIBLE);
        btnPersonalInfo.setVisibility(View.GONE);
    }

    private void showLoggedInUI(String username) {
        String[] greetings = {
                getString(R.string.greeting_hello, username),
                getString(R.string.greeting_cooking, username),
                getString(R.string.greeting_relax, username),
                getString(R.string.greeting_study, username),
        };

        int[] icons = {
                R.drawable.ic_ghost_smile,
                R.drawable.ic_flame,
                R.drawable.ic_tea_cup,
                R.drawable.ic_cat,
        };
        Random random = new Random();
        int index = random.nextInt(greetings.length);
        tvGreeting.setText(greetings[index]);
        ivAndroidLogo.setImageResource(icons[index]);

        btnLogin.setVisibility(View.GONE);
        btnPersonalInfo.setVisibility(View.VISIBLE);
    }

    private void loadUserInfo() {
        UserApi api = ApiClient.getInstance().create(UserApi.class);
        api.getCurrentUser().enqueue(new Callback<UserSummaryDTO>() {
            @Override
            public void onResponse(Call<UserSummaryDTO> call, Response<UserSummaryDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showLoggedInUI(response.body().username);
                } else {
                    showLoggedOutUI();
                }
            }

            @Override
            public void onFailure(Call<UserSummaryDTO> call, Throwable t) {
                showLoggedOutUI();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}

