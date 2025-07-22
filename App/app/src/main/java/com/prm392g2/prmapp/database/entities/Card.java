package com.prm392g2.prmapp.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(
    foreignKeys = {
        @androidx.room.ForeignKey(
            entity = Deck.class,
            parentColumns = { "id" },
            childColumns = { "deckId" },
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    }
)
public class Card
{
    @PrimaryKey
    public int id;
    public String front;
    public String back;
    public int index;
    @ColumnInfo(index = true)
    public int deckId;


    public Card()
    {
    }
}
