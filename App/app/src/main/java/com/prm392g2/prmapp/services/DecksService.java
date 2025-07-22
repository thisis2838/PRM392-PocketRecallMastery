package com.prm392g2.prmapp.services;

import android.content.Context;

import com.prm392g2.prmapp.api.DeckApi;
import com.prm392g2.prmapp.database.PRMDatabase;
import com.prm392g2.prmapp.dtos.decks.DeckDetailDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListArgumentsDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListDTO;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;
import com.prm392g2.prmapp.entities.Deck;
import com.prm392g2.prmapp.network.ApiClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DecksService
{
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Context context;
    public DecksService(Context context)
    {
        this.context = context;
    }

    public void getPublicDecks(DeckListArgumentsDTO args, Callback<DeckListDTO> callback)
    {
        DeckApi api = ApiClient.getInstance().create(DeckApi.class);
        Call<DeckListDTO> call = api.getPublicDecks(
            args.search,
            args.minCardCount,
            args.maxCardCount,
            args.sortingMetric,
            args.sortingAscending
        );
        call.enqueue(callback);
    }

    public void getDeckById(int id, Callback<DeckDetailDTO> callback)
    {
        DeckApi api = ApiClient.getInstance().create(DeckApi.class);
        var call = api.getDeckById(id);
        call.enqueue(callback);
    }

    public void getMyDecks(Callback<DeckListDTO> callback)
    {
        DeckApi api = ApiClient.getInstance().create(DeckApi.class);
        Call<DeckListDTO> call = api.getMyDecks();
        call.enqueue(callback);
    }

    public void saveDeck(int deckId, Runnable onComplete) {
        executor.execute(() -> {
            PRMDatabase.getInstance().deckDao().updateIsSaved(deckId, true);
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    public void getSavedDeckById(int deckId, Consumer<Deck> callback) {
        executor.execute(() -> {
            Deck deck = PRMDatabase.getInstance().deckDao().getById(deckId);
            if (callback != null) {
                callback.accept(deck);
            }
        });
    }

    public void getSavedDecks(java.util.function.Consumer<List<com.prm392g2.prmapp.entities.Deck>> callback) {
        executor.execute(() -> {
            List<com.prm392g2.prmapp.entities.Deck> savedDecks = PRMDatabase.getInstance().deckDao().getSavedDecks();
            if (callback != null) {
                callback.accept(savedDecks);
            }
        });
    }

    private static DecksService instance;
    public static void initialize(Context context)
    {
        instance = new DecksService(context);
    }
    public static DecksService getInstance()
    {
        if (instance == null)
        {
            throw new RuntimeException();
        }
        return instance;
    }
}
