package com.prm392g2.prmapp.dtos.decks;

public class DeckListArgumentsDTO
{
    public String search = null;
    public Integer minCardCount = null;
    public Integer maxCardCount = null;
    public DeckListMetric sortingMetric = DeckListMetric.Name;
    public boolean sortingAscending = true;
}
