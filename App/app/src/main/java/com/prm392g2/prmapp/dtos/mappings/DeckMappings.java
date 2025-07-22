package com.prm392g2.prmapp.dtos.mappings;

import com.prm392g2.prmapp.database.entities.Card;
import com.prm392g2.prmapp.database.entities.Deck;
import com.prm392g2.prmapp.database.entities.Learning;
import com.prm392g2.prmapp.database.entities.User;
import com.prm392g2.prmapp.dtos.decks.DeckDetailDTO;
import com.prm392g2.prmapp.dtos.decks.DeckSummaryDTO;

import java.util.List;
import java.util.stream.Collectors;

public class DeckMappings
{
    public static Deck DeckSummaryDTOToDeck(DeckSummaryDTO dto)
    {
        if (dto == null)
            return null;

        var deck = new Deck();
        deck.id = dto.id;
        deck.name = dto.name;
        deck.description = dto.description;
        deck.version = dto.version;
        deck.cardsCount = dto.cardsCount;
        deck.creatorId = dto.creator.id;
        deck.updatedAt = dto.getUpdatedAt();
        deck.createdAt = dto.getCreatedAt();

        return deck;
    }


    public static DeckSummaryDTO DeckToDeckSummaryDTO(Deck deck, User creator, Learning learning)
    {
        if (deck == null)
            return null;

        var dto = new DeckSummaryDTO();

        dto.id = deck.id;
        dto.name = deck.name;
        dto.description = deck.description;
        dto.version = deck.version;
        dto.cardsCount = deck.cardsCount;
        dto.setUpdatedAt(deck.updatedAt);
        dto.setCreatedAt(deck.createdAt);
        dto.creator = UserMappings.UserToUserSummaryDTO(creator);
        dto.learning = LearningMappings.LearningToLearningDetailDTO(learning);

        return dto;
    }

    public static DeckDetailDTO DeckToDeckDetailDTO(Deck deck, User creator, Learning learning, List<Card> cards)
    {
        if (deck == null)
            return null;

        var dto = new DeckDetailDTO();

        dto.id = deck.id;
        dto.name = deck.name;
        dto.description = deck.description;
        dto.version = deck.version;
        dto.cardsCount = deck.cardsCount;
        dto.setUpdatedAt(deck.updatedAt);
        dto.setCreatedAt(deck.createdAt);
        dto.creator = UserMappings.UserToUserSummaryDTO(creator);
        dto.learning = LearningMappings.LearningToLearningDetailDTO(learning);
        dto.cards = cards.stream().map(CardMappings::CardToCardDetailDTO).collect(Collectors.toList());

        return dto;
    }
}
