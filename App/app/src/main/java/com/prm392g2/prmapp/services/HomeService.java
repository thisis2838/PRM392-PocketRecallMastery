package com.prm392g2.prmapp.services;

import android.content.Context;

import com.prm392g2.prmapp.api.DeckApi;
import com.prm392g2.prmapp.api.HomeApi;
import com.prm392g2.prmapp.dtos.decks.DeckCreationDTO;
import com.prm392g2.prmapp.dtos.decks.DeckDetailDTO;
import com.prm392g2.prmapp.dtos.decks.DeckEditDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListArgumentsDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListDTO;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;
import com.prm392g2.prmapp.network.ApiClient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeService
{
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Context context;
    public HomeService(Context context)
    {
        this.context = context;
    }

    public void getRecentDecks(Callback<List<DeckSummaryDTO>> callback)
    {
        HomeApi api = ApiClient.getInstance().getHomeApi();
        var call = api.getRecentDecks();
        call.enqueue(callback);
    }

    public void getWeeklyPopularDecks(Callback<List<DeckSummaryDTO>> callback)
    {
        HomeApi api = ApiClient.getInstance().getHomeApi();
        var call = api.getWeeklyPopularDecks();
        call.enqueue(callback);
    }


    private static HomeService instance;
    public static void initialize(Context context)
    {
        instance = new HomeService(context);
    }
    public static HomeService getInstance()
    {
        if (instance == null)
        {
            throw new RuntimeException();
        }
        return instance;
    }
}
