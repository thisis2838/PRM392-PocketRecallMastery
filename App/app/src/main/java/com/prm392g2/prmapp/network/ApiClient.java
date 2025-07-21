package com.prm392g2.prmapp.network;

import android.content.Context;

import com.prm392g2.prmapp.api.DeckApi;
import com.prm392g2.prmapp.api.HomeApi;
import com.prm392g2.prmapp.api.UserApi;
import com.prm392g2.prmapp.network.interceptors.AuthInterceptor;
import com.prm392g2.prmapp.network.interceptors.UnauthorizedInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient
{
    private DeckApi deckApi;
    private HomeApi homeApi;
    private UserApi userApi;
    private Retrofit retrofit;

    public ApiClient(Context context, String baseUrl)
    {
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new AuthInterceptor(context))
            .addInterceptor(new UnauthorizedInterceptor(context))
            .build();

        this.retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    public DeckApi getDeckApi()
    {
        if (deckApi == null)
        {
            deckApi = retrofit.create(DeckApi.class);
        }
        return deckApi;
    }
    public HomeApi getHomeApi()
    {
        if (homeApi == null)
        {
            homeApi = retrofit.create(HomeApi.class);
        }
        return homeApi;
    }
    public UserApi getUserApi()
    {
        if (userApi == null)
        {
            userApi = retrofit.create(UserApi.class);
        }
        return userApi;
    }
    public Retrofit getRetrofit()
    {
        return retrofit;
    }

    private static ApiClient instance;
    public static void initialize(Context context, String url)
    {
        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new AuthInterceptor(context))
            .addInterceptor(new UnauthorizedInterceptor(context))
            .build();

        instance = new ApiClient(context, url);
    }

    public static ApiClient getInstance()
    {
        if (instance == null)
        {
            throw new RuntimeException();
        }
        return instance;
    }
}
