package com.prm392g2.prmapp.dtos.users;

public class UserSummaryDTO {
    public int Id;
    public String Username;
    public int DecksCount;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public int getDecksCount() {
        return DecksCount;
    }

    public void setDecksCount(int decksCount) {
        DecksCount = decksCount;
    }
}
