package com.prm392g2.prmapp.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;

import com.prm392g2.prmapp.database.entities.Card;

import java.util.List;

@Dao
public interface CardDao
{
    @Query("SELECT * FROM Card WHERE id = :id")
    Card getById(int id);

    @Query("SELECT * FROM Card WHERE deckId = :deckId")
    List<Card> getByDeckId(int deckId);

    @Insert
    void insert(Card card);

    @Insert
    void insertAll(List<Card> cards);

    @Update
    void update(Card card);

    @Delete
    void delete(Card card);
}