package com.prm392g2.prmapp.entities.extensions;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.prm392g2.prmapp.entities.Card;
import com.prm392g2.prmapp.entities.Deck;
import com.prm392g2.prmapp.entities.User;

import java.util.List;

public class DeckWithAll
{
    @Embedded
    public Deck deck;

    @Relation(
        parentColumn = "id",
        entityColumn = "deckId"
    )
    public List<Card> cards;

    @Relation(
        parentColumn = "creatorId",
        entityColumn = "id"
    )
    public User user;
}