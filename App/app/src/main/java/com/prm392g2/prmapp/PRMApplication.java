package com.prm392g2.prmapp;

import android.app.Application;
import android.content.Context;

import com.prm392g2.prmapp.database.PRMDatabase;
import com.prm392g2.prmapp.network.ApiClient;
import com.prm392g2.prmapp.services.DecksService;
import com.prm392g2.prmapp.services.HomeService;
import com.prm392g2.prmapp.services.SavedDecksService;
import com.prm392g2.prmapp.services.UserWeeklyStatsService;
import com.prm392g2.prmapp.services.UsersService;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.prm392g2.prmapp.database.PRMDatabase;
import com.prm392g2.prmapp.helpers.LocaleHelper;
import com.prm392g2.prmapp.network.ApiClient;
import com.prm392g2.prmapp.services.DecksService;
import com.prm392g2.prmapp.services.UsersService;


import java.text.SimpleDateFormat;

public class PRMApplication extends Application
{
    public static final SimpleDateFormat GLOBAL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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

        context = this;
        try
        {
            PRMDatabase.initialize(this);
            ApiClient.initialize( this, "http://10.0.2.2:5047/");
            UsersService.initialize(this);
            DecksService.initialize(this);
            HomeService.initialize(this);
            SavedDecksService.initialize(this);
            UserWeeklyStatsService.initialize(this);
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
