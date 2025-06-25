package com.prm392g2.prmapp.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.prm392g2.prmapp.database.daos.CardDao;
import com.prm392g2.prmapp.database.daos.DeckDao;
import com.prm392g2.prmapp.database.daos.LearningDao;
import com.prm392g2.prmapp.database.daos.UserDao;
import com.prm392g2.prmapp.entities.Card;
import com.prm392g2.prmapp.entities.Deck;
import com.prm392g2.prmapp.entities.Learning;
import com.prm392g2.prmapp.entities.User;

@Database(entities = { User.class, Deck.class, Card.class, Learning.class }, version = 1)
@TypeConverters({ Converters.class })
public abstract class PRMDatabase extends RoomDatabase
{
    public static final String DATABASE_NAME = "pocketrecallmastery.sqlite";

    public abstract UserDao userDao();
    public abstract DeckDao deckDao();
    public abstract CardDao cardDao();
    public abstract LearningDao learningDao();

    private Context context;

    public static void initialize(Context context) throws Throwable
    {
        if (instance != null)
        {
            instance.finalize();
        }

        instance = Room.databaseBuilder(context.getApplicationContext(), PRMDatabase.class, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build();
        instance.context = context;
    }

    private static volatile PRMDatabase instance;
    public static PRMDatabase getInstance()
    {
        assert instance != null;
        return instance;
    }
}
