package com.prm392g2.prmapp.api;

import com.prm392g2.prmapp.dtos.decks.DeckCreationDTO;
import com.prm392g2.prmapp.dtos.decks.DeckDetailDTO;
import com.prm392g2.prmapp.dtos.decks.DeckEditDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListMetric;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DeckApi {
    @GET("api/Decks/public")
    Call<DeckListDTO> getPublic
    (
        @Query("search") String search,
        @Query("minCardCount") Integer minCardCount,
        @Query("maxCardCount") Integer maxCardCount,
        @Query("sortingMetric") DeckListMetric sortingMetric,
        @Query("sortingAscending") boolean sortingAscending
    );

    @GET("api/Decks/{id}")
    Call<DeckDetailDTO> getById(@Path("id") int id);

    @GET("api/Decks/my")
    Call<DeckListDTO> getMine();

    @PUT("api/Decks")
    Call<Integer> create(@Body DeckCreationDTO dto);

    @POST("api/Decks/{id}")
    Call<Void> update(@Path("id") int id, @Body DeckEditDTO dto);

    @DELETE("api/Decks/{id}")
    Call<Void> delete(@Path("id") int id);
}
