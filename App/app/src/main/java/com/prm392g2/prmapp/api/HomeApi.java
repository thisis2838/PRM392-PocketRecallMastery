package com.prm392g2.prmapp.api;

import com.prm392g2.prmapp.dtos.decks.DeckCreationDTO;
import com.prm392g2.prmapp.dtos.decks.DeckDetailDTO;
import com.prm392g2.prmapp.dtos.decks.DeckEditDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListMetric;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HomeApi
{
    @GET("api/Home/recent")
    Call<List<DeckSummaryDTO>> getRecentDecks();

    @GET("api/Home/popular/weekly")
    Call<List<DeckSummaryDTO>> getWeeklyPopularDecks();
}
