package com.prm392g2.prmapp.network.interceptors;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.prm392g2.prmapp.PRMApplication;
import com.prm392g2.prmapp.activities.LoginActivity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class UnauthorizedInterceptor implements Interceptor
{
    private Context context;

    public UnauthorizedInterceptor(Context context)
    {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Response response = chain.proceed(chain.request());

        if (response.code() == 401)
        {
            new Handler(Looper.getMainLooper()).post(() ->
            {
                if (!LoginActivity.isActive)
                {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            });
        }

        return response;
    }
}
