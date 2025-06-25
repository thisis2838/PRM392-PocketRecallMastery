package com.prm392g2.prmapp;

import android.app.Application;

import com.prm392g2.prmapp.database.PRMDatabase;

public class PRMApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        try
        {
            PRMDatabase.initialize(this);
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }
    }
}
