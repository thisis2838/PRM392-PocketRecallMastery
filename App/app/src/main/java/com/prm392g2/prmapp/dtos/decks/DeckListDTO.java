package com.prm392g2.prmapp.dtos.decks;

import java.util.List;

public class DeckListDTO {

    public int TotalCount;
    public List<DeckSummaryDTO> Decks;

    public List<DeckSummaryDTO> getDecks() {
        return Decks;
    }

    public void setDecks(List<DeckSummaryDTO> decks) {
        Decks = decks;
    }

    public int getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(int totalCount) {
        TotalCount = totalCount;
    }
}
