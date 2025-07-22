package com.prm392g2.prmapp.services;

import android.content.Context;

import com.prm392g2.prmapp.database.PRMDatabase;
import com.prm392g2.prmapp.entities.Card;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import retrofit2.Callback;

public class CardService {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Context context;
    public CardService(Context context)
    {
        this.context = context;
    }

    public void getCardsByDeckId(int deckId, Consumer<List<Card>> callback){
        executor.execute(() -> {
            List<Card> cards = PRMDatabase.getInstance().cardDao().getByDeckId(deckId);
            if (callback != null) callback.accept(cards);
        });
    }

    private static CardService instance;
    public static void initialize(Context context)
    {
        instance = new CardService(context);
    }
    public static CardService getInstance()
    {
        if (instance == null)
        {
            throw new RuntimeException();
        }
        return instance;
    }
}
