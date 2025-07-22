package com.prm392g2.prmapp.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;

import com.prm392g2.prmapp.database.entities.Deck;
import com.prm392g2.prmapp.database.entities.extensions.DeckWithAll;
import com.prm392g2.prmapp.database.entities.extensions.DeckWithUserAndLearning;

import java.util.List;

@Dao
public interface DeckDao
{
    @Query("SELECT * FROM Deck")
    List<DeckWithUserAndLearning> getAllWithUserAndLearning();

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

    @Query("DELETE FROM Deck WHERE id = :id")
    void deleteById(int id);
}