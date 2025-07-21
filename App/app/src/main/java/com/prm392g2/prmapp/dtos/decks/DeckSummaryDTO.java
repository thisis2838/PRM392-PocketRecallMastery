package com.prm392g2.prmapp.dtos.decks;

import com.prm392g2.prmapp.dtos.users.UserSummaryDTO;

import java.util.GregorianCalendar;

public class DeckSummaryDTO {
    public int Id;
    public String Name;
    public String Description;
    public boolean IsPublic;
    public int Version;
    public int ViewsTotal;
    public int ViewsWeekly;
    public int DownloadsWeekly;
    public int DownloadsTotal;
    public GregorianCalendar CreatedAt;
    public int CardsCount;
    public UserSummaryDTO Creator;

    public int getDownloadsTotal() {
        return DownloadsTotal;
    }

    public void setDownloadsTotal(int downloadsTotal) {
        DownloadsTotal = downloadsTotal;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getVersion() {
        return Version;
    }

    public void setVersion(int version) {
        Version = version;
    }

    public boolean isPublic() {
        return IsPublic;
    }

    public void setPublic(boolean aPublic) {
        IsPublic = aPublic;
    }

    public int getViewsTotal() {
        return ViewsTotal;
    }

    public void setViewsTotal(int viewsTotal) {
        ViewsTotal = viewsTotal;
    }

    public int getDownloadsWeekly() {
        return DownloadsWeekly;
    }

    public void setDownloadsWeekly(int downloadsWeekly) {
        DownloadsWeekly = downloadsWeekly;
    }

    public int getViewsWeekly() {
        return ViewsWeekly;
    }

    public void setViewsWeekly(int viewsWeekly) {
        ViewsWeekly = viewsWeekly;
    }

    public GregorianCalendar getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(GregorianCalendar createdAt) {
        CreatedAt = createdAt;
    }

    public int getCardsCount() {
        return CardsCount;
    }

    public void setCardsCount(int cardsCount) {
        CardsCount = cardsCount;
    }

    public UserSummaryDTO getCreator() {
        return Creator;
    }

    public void setCreator(UserSummaryDTO creator) {
        Creator = creator;
    }
}
