package com.prm392g2.prmapp.services;

import android.content.Context;

import com.prm392g2.prmapp.api.DeckApi;
import com.prm392g2.prmapp.dtos.decks.DeckCreationDTO;
import com.prm392g2.prmapp.dtos.decks.DeckDetailDTO;
import com.prm392g2.prmapp.dtos.decks.DeckEditDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListArgumentsDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListDTO;
import com.prm392g2.prmapp.network.ApiClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;

public class DecksService
{
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Context context;
    public DecksService(Context context)
    {
        this.context = context;
    }

    public void getPublic(DeckListArgumentsDTO args, Callback<DeckListDTO> callback)
    {
        DeckApi api = ApiClient.getInstance().create(DeckApi.class);
        Call<DeckListDTO> call = api.getPublic(
            args.search,
            args.minCardCount,
            args.maxCardCount,
            args.sortingMetric,
            args.sortingAscending
        );
        call.enqueue(callback);
    }

    public void getById(int id, Callback<DeckDetailDTO> callback)
    {
        DeckApi api = ApiClient.getInstance().create(DeckApi.class);
        var call = api.getById(id);
        call.enqueue(callback);
    }

    public void getMine(Callback<DeckListDTO> callback)
    {
        DeckApi api = ApiClient.getInstance().create(DeckApi.class);
        Call<DeckListDTO> call = api.getMine();
        call.enqueue(callback);
    }

    public void create(DeckCreationDTO deck, Callback<Integer> callback)
    {
        DeckApi api = ApiClient.getInstance().create(DeckApi.class);
        var call = api.create(deck);
        call.enqueue(callback);
    }

    public void edit(int id, DeckEditDTO deck, Callback<Void> callback)
    {
        DeckApi api = ApiClient.getInstance().create(DeckApi.class);
        var call = api.update(id, deck);
        call.enqueue(callback);
    }

    public void delete(int id, Callback<Void> callback)
    {
        DeckApi api = ApiClient.getInstance().create(DeckApi.class);
        var call = api.delete(id);
        call.enqueue(callback);
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
