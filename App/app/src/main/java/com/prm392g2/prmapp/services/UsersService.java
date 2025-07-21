package com.prm392g2.prmapp.services;

import android.content.Context;

import com.prm392g2.prmapp.PRMApplication;
import com.prm392g2.prmapp.api.UserApi;
import com.prm392g2.prmapp.dtos.users.LoginRequestDTO;
import com.prm392g2.prmapp.dtos.users.LoginResponseDTO;
import com.prm392g2.prmapp.network.ApiClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersService
{
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Context context;

    public UsersService(Context context)
    {
        this.context = context;
    }

    public void login(String username, String password, Callback<LoginResponseDTO> callback)
    {
        LoginRequestDTO request = new LoginRequestDTO(username, password);
        UserApi api = ApiClient.getInstance().getUserApi();
        Call<LoginResponseDTO> call = api.login(request);
        call.enqueue(new Callback<LoginResponseDTO>()
        {
            @Override
            public void onResponse(Call<LoginResponseDTO> call, Response<LoginResponseDTO> response)
            {
                if (response.isSuccessful())
                {
                    LoginResponseDTO loginResponse = response.body();
                    if (loginResponse != null && loginResponse.token != null)
                    {
                        PRMApplication.getContext()
                            .getSharedPreferences("auth", Context.MODE_PRIVATE)
                            .edit()
                            .putString("token", loginResponse.token)
                            .apply();
                    }
                }

                if (callback != null)
                {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponseDTO> call, Throwable t)
            {
                if (callback != null)
                {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    private static UsersService instance;

    public static void initialize(Context context)
    {
        instance = new UsersService(context);
    }

    public static UsersService getInstance()
    {
        if (instance == null)
        {
            throw new RuntimeException();
        }
        return instance;
    }
}
