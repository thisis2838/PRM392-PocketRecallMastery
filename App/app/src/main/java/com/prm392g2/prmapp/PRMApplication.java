package com.prm392g2.prmapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.prm392g2.prmapp.database.PRMDatabase;
import com.prm392g2.prmapp.network.ApiClient;
import com.prm392g2.prmapp.services.DecksService;
import com.prm392g2.prmapp.services.UsersService;

public class PRMApplication extends Application
{
    private static Context context;

    @Override
    public void onCreate()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
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
