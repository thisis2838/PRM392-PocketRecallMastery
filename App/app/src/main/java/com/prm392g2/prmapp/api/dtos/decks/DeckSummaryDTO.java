package com.prm392g2.prmapp.api.dtos.decks;

import com.prm392g2.prmapp.api.dtos.users.UserSummaryDTO;

import java.util.GregorianCalendar;

public class DeckSummaryDTO
{
    public int id;
    public String name;
    public String description;
    public boolean isPublic;
    public int version;
    public int viewsTotal;
    public int viewsWeekly;
    public int downloadsTotal;
    public int downloadsWeekly;
    public GregorianCalendar createdAt;
    public int cardsCount;
    public UserSummaryDTO creator;

}
