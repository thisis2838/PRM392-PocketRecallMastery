package com.prm392g2.prmapp.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User
{
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String username;

    public User(int id, String username)
    {
        this.id = id;
        this.username = username;
    }
    public User() {}
}
