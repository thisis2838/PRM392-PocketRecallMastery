package com.prm392g2.prmapp;

import android.app.Application;
import android.content.Context;

import com.prm392g2.prmapp.database.PRMDatabase;
import com.prm392g2.prmapp.network.ApiClient;
import com.prm392g2.prmapp.services.DecksService;
import com.prm392g2.prmapp.services.UsersService;

import java.text.SimpleDateFormat;

public class PRMApplication extends Application
{
    public static final SimpleDateFormat GLOBAL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static Context context;

    @Override
    public void onCreate()
    {
        super.onCreate();

        context = this;
        try
        {
            PRMDatabase.initialize(this);
            ApiClient.initialize(this);
            UsersService.initialize(this);
            DecksService.initialize(this);
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

    public static Context getContext()
    {
        return context;
    }
}
