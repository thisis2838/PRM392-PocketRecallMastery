package com.prm392g2.prmapp.api.dtos.decks;

import com.prm392g2.prmapp.api.dtos.cards.CardDetailDTO;

import java.util.List;

public class DeckDetailDTO extends DeckSummaryDTO
{
    public List<CardDetailDTO> cards;
}
