package com.prm392g2.prmapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.prm392g2.prmapp.R;

public class ProfileFragment extends Fragment {

    private TextView tvUserInfo;
    private EditText etJwt;
    private Button btnSettings, btnPersonalInfo;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUserInfo = view.findViewById(R.id.tvUserInfo);
        etJwt = view.findViewById(R.id.etJwt);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnPersonalInfo = view.findViewById(R.id.btnPersonalInfo);

        loadJwt();
        loadUserInfo();

        btnSettings.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .addToBackStack(null)
                        .commit());

        btnPersonalInfo.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new PersonalInfoFragment())
                        .addToBackStack(null)
                        .commit());

        return view;
    }

    private void loadJwt() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt_token", "Not logged in");
        etJwt.setText(token);
    }

    private void loadUserInfo() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "Unknown");
        String email = prefs.getString("email", "Unknown");
        String language = prefs.getString("language", "None");
        String theme = prefs.getString("theme", "Default");
        boolean notif = prefs.getBoolean("notification", false);

        String info = "Username: " + username + "\n"
                + "Email: " + email + "\n"
                + "Language: " + language + "\n"
                + "Theme: " + theme + "\n"
                + "Notifications: " + (notif ? "On" : "Off");

        tvUserInfo.setText(info);
    }
}