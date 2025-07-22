package com.prm392g2.prmapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;

import com.prm392g2.prmapp.R;
import com.prm392g2.prmapp.activities.MainActivity;
import com.prm392g2.prmapp.helpers.LocaleHelper;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String PREF_KEY_LAST_FRAGMENT = "last_fragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        ((MainActivity) requireActivity()).setToolbarTitle(getString(R.string.settings_title));
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
        if ("language".equals(key)) {
            String lang = sharedPrefs.getString("language", "en");
            LocaleHelper.setLocale(requireContext(), lang);
            persistLastFragment();
            requireActivity().recreate();
        } else if ("dark_mode".equals(key)) {
            boolean isDark = sharedPrefs.getBoolean("dark_mode", false);
            AppCompatDelegate.setDefaultNightMode(
                    isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            persistLastFragment();
            requireActivity().recreate();
        }
    }

    private void persistLastFragment() {
        SharedPreferences.Editor editor = requireContext()
                .getSharedPreferences("prefs", Context.MODE_PRIVATE).edit();
        editor.putString(PREF_KEY_LAST_FRAGMENT, "SettingsFragment");
        editor.apply();
    }

    public static boolean shouldRestore(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        return "SettingsFragment".equals(prefs.getString(PREF_KEY_LAST_FRAGMENT, null));
    }

    public static void clearRestoreFlag(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        prefs.edit().remove(PREF_KEY_LAST_FRAGMENT).apply();
    }
}
