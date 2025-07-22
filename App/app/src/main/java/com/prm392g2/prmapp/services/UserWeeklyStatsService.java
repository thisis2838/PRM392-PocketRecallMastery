package com.prm392g2.prmapp.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.prm392g2.prmapp.PRMApplication;

import java.util.GregorianCalendar;

public class UserWeeklyStatsService
{
    private Context context;
    private final static String SHARED_PREFS_NAME = "user_stats";
    private final static String CURRENT_WEEK_KEY = "current_week";
    private final static String DECK_VIEWS_WEEKLY_KEY = "deck_views_weekly";
    private final static String DECKS_LEARNED_WEEKLY_KEY = "decks_learned_weekly";
    private final static String CARDS_TURNED_WEEKLY_KEY = "cards_turned_weekly";
    private SharedPreferences sharedPreferences;

    public UserWeeklyStatsService(Context context)
    {
        this.context = context;
    }

    private SharedPreferences getUserStatsPreferences()
    {
        if (sharedPreferences != null)
        {
            return sharedPreferences;
        }

        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        GregorianCalendar cal = new GregorianCalendar();
        cal.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
        var curWeekMondayStr = PRMApplication.GLOBAL_DATE_FORMAT.format(cal.getTime());
        if (sharedPreferences.contains(CURRENT_WEEK_KEY))
        {
            var prefsWeekMondayStr = sharedPreferences.getString(CURRENT_WEEK_KEY, "");
            if (!curWeekMondayStr.equals(prefsWeekMondayStr))
            {
                sharedPreferences.edit()
                    .putInt("deck_views_weekly", 0)
                    .putInt("decks_learned_weekly", 0)
                    .putInt("cards_turned_weekly", 0)
                    .apply();
            }
        }
        sharedPreferences.edit()
            .putString(CURRENT_WEEK_KEY, curWeekMondayStr)
            .apply();

        return sharedPreferences;
    }

    public int getDeckViews()
    {
        return getUserStatsPreferences()
            .getInt(DECK_VIEWS_WEEKLY_KEY, 0);
    }
    public void incrementDeckViews()
    {
        getUserStatsPreferences()
            .edit()
            .putInt(DECK_VIEWS_WEEKLY_KEY, getDeckViews() + 1)
            .apply();
    }

    public int getDecksLearned()
    {
        return getUserStatsPreferences()
            .getInt(DECKS_LEARNED_WEEKLY_KEY, 0);
    }
    public void incrementDecksLearned()
    {
        getUserStatsPreferences()
            .edit()
            .putInt(DECKS_LEARNED_WEEKLY_KEY, getDecksLearned() + 1)
            .apply();
    }

    public int getCardsTurned()
    {
        return getUserStatsPreferences()
            .getInt(CARDS_TURNED_WEEKLY_KEY, 0);
    }
    public void incrementCardsTurned()
    {
        getUserStatsPreferences()
            .edit()
            .putInt(CARDS_TURNED_WEEKLY_KEY, getCardsTurned() + 1)
            .apply();
    }

    private static UserWeeklyStatsService instance;
    public static void initialize(Context context)
    {
        instance = new UserWeeklyStatsService(context);
    }
    public static UserWeeklyStatsService getInstance()
    {
        if (instance == null)
        {
            throw new RuntimeException();
        }
        return instance;
    }
}
