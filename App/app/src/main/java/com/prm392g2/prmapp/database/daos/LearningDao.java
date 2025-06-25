package com.prm392g2.prmapp.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;

import com.prm392g2.prmapp.entities.Learning;

import java.util.List;

@Dao
public interface LearningDao
{
    @Query("SELECT * FROM Learning")
    List<Learning> getAll();

    @Query("SELECT * FROM Learning WHERE id = :id")
    Learning getById(int id);

    @Query("SELECT * FROM Learning WHERE deckId = :deckId")
    List<Learning> getByDeckId(int deckId);

    @Insert
    void insert(Learning learning);

    @Update
    void update(Learning learning);

    @Delete
    void delete(Learning learning);
}