package com.prm392g2.prmapp.database.entities.extensions;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.prm392g2.prmapp.database.entities.Deck;
import com.prm392g2.prmapp.database.entities.Learning;
import com.prm392g2.prmapp.database.entities.User;

public class DeckWithUserAndLearning
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
        entityColumn = "deckId"
    )
    public Learning learning;
}