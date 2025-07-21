package com.prm392g2.prmapp.dialogs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.activities.VerifyOtpActivity;
import com.prm392g2.prmapp.api.UserApi;
import com.prm392g2.prmapp.dtos.users.EmailChangeDTO;
import com.prm392g2.prmapp.network.ApiClient;

import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailChangeDialog extends DialogFragment {

    private EditText etNewEmail;
    private Button btnSubmit;
    private final Consumer<String> onSubmitCallback;

    public EmailChangeDialog(Consumer<String> onSubmitCallback) {
        this.onSubmitCallback = onSubmitCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_change_email, container, false);

        etNewEmail = view.findViewById(R.id.etNewEmail);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            String newEmail = etNewEmail.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                etNewEmail.setError("Invalid email format");
                return;
            }

            btnSubmit.setEnabled(false);
            sendEmailChangeRequest(newEmail);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void sendEmailChangeRequest(String email) {
        String token = getContext()
                .getSharedPreferences("auth", Context.MODE_PRIVATE)
                .getString("token", null);

        if (token == null) {
            Toast.makeText(getContext(), "You must be logged in", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        EmailChangeDTO dto = new EmailChangeDTO(email);
        UserApi api = ApiClient.getInstance().create(UserApi.class);

        Call<Void> call = api.requestEmailChange("Bearer " + token, dto);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnSubmit.setEnabled(true);
                if (response.isSuccessful()) {
                    dismiss();
                    if (getContext() != null) {
                        Intent intent = new Intent(getContext(), VerifyOtpActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("purpose", "change-email");
                        getContext().startActivity(intent);
                    }
                } else {
                    etNewEmail.setError("Email already in use or invalid");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSubmit.setEnabled(true);
                Toast.makeText(getContext(), "Request failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}