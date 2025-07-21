package com.prm392g2.prmapp.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class LocaleHelper {

    public static Context setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        return updateResources(context, locale);
    }

    private static Context updateResources(Context context, Locale locale) {
        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        config.setLayoutDirection(locale);
        return context.createConfigurationContext(config);
    }
}

