package com.prm392g2.prmapp.api;

import com.prm392g2.prmapp.dtos.decks.DeckDetailDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListArgumentsDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListMetric;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DeckApi {
    @GET("api/Decks/public")
    Call<DeckListDTO> getPublicDecks
    (
        @Query("search") String search,
        @Query("minCardCount") Integer minCardCount,
        @Query("maxCardCount") Integer maxCardCount,
        @Query("sortingMetric") DeckListMetric sortingMetric,
        @Query("sortingAscending") boolean sortingAscending
    );

    @GET("api/Decks/{id}")
    Call<DeckDetailDTO> getDeckById(@Path("id") int id);

    @GET("api/Decks/my")
    Call<DeckListDTO> getMyDecks();
}
