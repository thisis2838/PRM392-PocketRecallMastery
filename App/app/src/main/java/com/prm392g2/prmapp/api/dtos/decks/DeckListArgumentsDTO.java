package com.prm392g2.prmapp.api.dtos.decks;

public class DeckListArgumentsDTO
{
    public String search;
    public Integer minCardCount;
    public Integer maxCardCount;
    public DeckListMetric sortingMetric;
    public boolean sortingAscending;
    public int pageIndex;
    public int pageSize;
}
