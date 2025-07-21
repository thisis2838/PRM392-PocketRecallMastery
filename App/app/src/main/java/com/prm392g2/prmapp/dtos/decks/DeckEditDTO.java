package com.prm392g2.prmapp.dtos.decks;

import com.prm392g2.prmapp.dtos.cards.CardDetailDTO;

import java.util.List;

public class DeckEditDTO
{
    public String name;
    public String description;
    public boolean isPublic;
    public List<CardDetailDTO> cards;
}
