package com.prm392g2.prmapp.entities;

import androidx.room.Entity;
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
    },
    indices = {
        @androidx.room.Index("creatorId"),
        @androidx.room.Index("learningId")
    }
)
public class Deck
{
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String description;
    public int creatorId;
    public int version;
    public boolean isSaved = false;
    public GregorianCalendar createdAt;
    public GregorianCalendar updatedAt = null;
    public Integer learningId = null;

    public Deck(int id, String name, String description, int creatorId, int version, GregorianCalendar createdAt)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
        this.version = version;
        this.createdAt = createdAt;
    }

    public Deck()
    {
    }
}
