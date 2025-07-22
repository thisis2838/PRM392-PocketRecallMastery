package com.prm392g2.prmapp.database.entities.extensions;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.prm392g2.prmapp.database.entities.Card;
import com.prm392g2.prmapp.database.entities.Deck;
import com.prm392g2.prmapp.database.entities.Learning;
import com.prm392g2.prmapp.database.entities.User;

import java.util.List;

public class DeckWithAll
{
    @Embedded
    public Deck deck;

    @Relation(
        parentColumn = "creatorId",
        entityColumn = "id"
    )
    public User creator;

    @Relation(
        parentColumn = "learningId",
        entityColumn = "id"
    )
    public Learning learning;

    @Relation(
        parentColumn = "id",
        entityColumn = "deckId"
    )
    public List<Card> cards;
}