package com.prm392g2.prmapp.network;

import android.content.Context;

import com.prm392g2.prmapp.network.interceptors.AuthInterceptor;
import com.prm392g2.prmapp.network.interceptors.UnauthorizedInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient
{
    private static final String BASE_URL = "http://10.0.2.2:5047/";
    private static Retrofit retrofit;

    public static void initialize(Context context)
    {
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new AuthInterceptor(context))
            .addInterceptor(new UnauthorizedInterceptor(context))
            .build();
        retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    public static Retrofit getInstance()
    {
        if (retrofit == null)
        {
            throw new RuntimeException();
        }
        return retrofit;
    }
}
