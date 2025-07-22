package com.prm392g2.prmapp.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User
{
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String username;

    public User() {}
}
