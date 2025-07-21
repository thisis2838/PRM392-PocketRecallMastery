package com.prm392g2.prmapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.prm392g2.prmapp.database.PRMDatabase;
import com.prm392g2.prmapp.helpers.LocaleHelper;
import com.prm392g2.prmapp.network.ApiClient;
import com.prm392g2.prmapp.services.DecksService;
import com.prm392g2.prmapp.services.UsersService;

public class PRMApplication extends Application
{
    private static Context context;

    @Override
    protected void attachBaseContext(Context base) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(base);
        String lang = prefs.getString("language", "en");
        Context localizedContext = LocaleHelper.setLocale(base, lang);
        super.attachBaseContext(localizedContext);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = this;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

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
