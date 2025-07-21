package com.prm392g2.prmapp.api;

import com.prm392g2.prmapp.dtos.decks.DeckDetailDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListArgumentsDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DeckApi {
    @GET("api/Decks/public")
    Call<DeckListDTO> getPublicDecks(
            @Query("Search") String search,
            @Query("MinCardCount") Integer minCardCount,
            @Query("MaxCardCount") Integer maxCardCount,
            @Query("SortingMetric") String sortingMetric,
            @Query("SortingAscending") boolean sortingAscending
    );

    @GET("api/Decks/{id}")
    Call<DeckDetailDTO> getDeckById(@Path("id") String id);
}
