package com.prm392g2.prmapp.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

@Entity
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

    public Learning(int id, int deckId, int currentCardIndex, int seed, GregorianCalendar lastLearnt, List<Integer> hardCardIndexes, boolean inverted)
    {
        this.id = id;
        this.deckId = deckId;
        this.currentCardIndex = currentCardIndex;
        this.seed = seed;
        this.lastLearnt = lastLearnt;
        this.hardCardIndexes = hardCardIndexes.stream().collect(Collectors.toList());
        this.inverted = inverted;
    }
    public Learning() {}
}
