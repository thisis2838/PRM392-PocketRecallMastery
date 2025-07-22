package com.prm392g2.prmapp.dtos.mappings;

import com.prm392g2.prmapp.database.entities.Card;
import com.prm392g2.prmapp.dtos.cards.CardDetailDTO;

public class CardMappings
{
    public static CardDetailDTO CardToCardDetailDTO(com.prm392g2.prmapp.database.entities.Card card)
    {
        if (card == null)
            return null;

        CardDetailDTO dto = new CardDetailDTO();
        dto.id = card.id;
        dto.front = card.front;
        dto.back = card.back;
        dto.index = card.index;
        return dto;
    }

    public static Card CardDetailDTOToCard(CardDetailDTO dto, int deckId)
    {
        if (dto == null)
            return null;

        Card card = new Card();
        card.id = dto.id;
        card.front = dto.front;
        card.back = dto.back;
        card.index = dto.index;
        card.deckId = deckId;

        return card;
    }
}
