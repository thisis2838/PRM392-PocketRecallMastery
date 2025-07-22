package com.prm392g2.prmapp.network.interceptors;

import android.content.Context;

import com.prm392g2.prmapp.PRMApplication;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor
{
    private Context context;

    public AuthInterceptor(Context context)
    {
        this.context = context;
    }


    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request original = chain.request();
        var token = context
            .getSharedPreferences("auth", Context.MODE_PRIVATE)
            .getString("token", null);

        Request.Builder builder = original.newBuilder();
        if (token != null && !token.isEmpty())
        {
            builder.header("Authorization", "Bearer " + token);
        }

        Request request = builder.build();
        return chain.proceed(request);
    }
}
