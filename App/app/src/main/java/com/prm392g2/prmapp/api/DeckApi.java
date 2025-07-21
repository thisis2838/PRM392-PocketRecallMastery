package com.prm392g2.prmapp.api;

import com.prm392g2.prmapp.dtos.decks.DeckListArgumentsDTO;
import com.prm392g2.prmapp.dtos.decks.DeckListDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DeckApi {
    @GET("api/Decks/public")
    Call<DeckListDTO> getPublicDecks(@Query("DeckListArgumentsDTO") DeckListArgumentsDTO deckListArgumentsDTO);
}
