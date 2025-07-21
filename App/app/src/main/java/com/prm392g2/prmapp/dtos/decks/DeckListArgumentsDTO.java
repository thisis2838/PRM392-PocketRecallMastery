package com.prm392g2.prmapp.dtos.decks;

public class DeckListArgumentsDTO {
    private String search = null;
    private Integer minCardCount = null;
    private Integer maxCardCount = null;
    private DeckListMetric sortingMetric = DeckListMetric.Name;
    private boolean sortingAscending = true;
    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Integer getMinCardCount() {
        return minCardCount;
    }

    public void setMinCardCount(Integer minCardCount) {
        this.minCardCount = minCardCount;
    }

    public Integer getMaxCardCount() {
        return maxCardCount;
    }

    public void setMaxCardCount(Integer maxCardCount) {
        this.maxCardCount = maxCardCount;
    }

    public DeckListMetric getSortingMetric() {
        return sortingMetric;
    }

    public void setSortingMetric(DeckListMetric sortingMetric) {
        this.sortingMetric = sortingMetric;
    }

    public boolean isSortingAscending() {
        return sortingAscending;
    }

    public void setSortingAscending(boolean sortingAscending) {
        this.sortingAscending = sortingAscending;
    }
}
