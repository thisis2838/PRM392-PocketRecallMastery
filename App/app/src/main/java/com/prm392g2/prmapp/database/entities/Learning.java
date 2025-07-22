package com.prm392g2.prmapp.database.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

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
public class Learning
{
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int deckId;
    public int currentCardIndex;
    public int seed;
    public GregorianCalendar lastLearnt;
    public List<Integer> hardCardIndexes;
    public boolean inverted;

    public Learning() {}
}
