package com.prm392g2.prmapp.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;
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
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String description;
    public int creatorId;
    public int version;
    public int dateYear, dateMonth, dateDay;
    public Integer learningId = null;

    public Deck(int id, String name, String description, int creatorId, int version, GregorianCalendar date)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
        this.version = version;
        this.dateYear = date.get(Calendar.YEAR);
        this.dateMonth = date.get(Calendar.MONTH);
        this.dateDay = date.get(Calendar.DAY_OF_MONTH);
    }

    public Deck()
    {
    }

    public GregorianCalendar getDate()
    {
        return new GregorianCalendar(dateYear, dateMonth, dateDay);
    }
}
