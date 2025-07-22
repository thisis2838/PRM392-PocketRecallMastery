package com.prm392g2.prmapp.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.GregorianCalendar;

@Entity(
    foreignKeys = {
        @androidx.room.ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "creatorId",
            onDelete = androidx.room.ForeignKey.CASCADE
        ),
        @androidx.room.ForeignKey(
            entity = Learning.class,
            parentColumns = "id",
            childColumns = "learningId",
            onDelete = androidx.room.ForeignKey.SET_NULL
        )
    }
)
public class Deck
{
    @PrimaryKey
    public int id;

    public String name;
    public String description;
    public int cardsCount;
    public int version;
    public GregorianCalendar createdAt;
    public GregorianCalendar updatedAt = null;
    @ColumnInfo(index = true)
    public Integer learningId = null;
    @ColumnInfo(index = true)
    public int creatorId;

    public Deck()
    {
    }
}
