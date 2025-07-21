package com.prm392g2.prmapp.dtos.decks;

import com.prm392g2.prmapp.dtos.users.UserSummaryDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
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
    public int downloadsWeekly;
    public int downloadsTotal;
    private String createdAt;
    public int cardsCount;
    public UserSummaryDTO creator;

    public GregorianCalendar getCreatedAt()
    {
        if (createdAt == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try
        {
            Date date = sdf.parse(createdAt);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return calendar;
        }
        catch (ParseException e)
        {
            return null;
        }
    }
}
