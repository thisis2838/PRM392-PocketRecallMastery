package com.prm392g2.prmapp.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;

import com.prm392g2.prmapp.entities.Deck;
import com.prm392g2.prmapp.entities.extensions.DeckWithAll;

import java.util.List;

@Dao
public interface DeckDao
{
    @Query("SELECT * FROM Deck")
    List<Deck> getAll();

    @Query("SELECT * FROM Deck WHERE id = :id")
    Deck getById(int id);

    @Query("SELECT * FROM Deck WHERE id = :id")
    DeckWithAll getByIdWithAll(int id);

    @Insert
    void insert(Deck deck);

    @Update
    void update(Deck deck);

    @Delete
    void delete(Deck deck);
}