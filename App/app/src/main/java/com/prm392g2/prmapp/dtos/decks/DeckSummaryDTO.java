package com.prm392g2.prmapp.dtos.decks;

import com.prm392g2.prmapp.PRMApplication;
import com.prm392g2.prmapp.dtos.learnings.LearningDetailDTO;
import com.prm392g2.prmapp.dtos.users.UserSummaryDTO;
import com.prm392g2.prmapp.utilities.helpers.DateTimeHelpers;

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
    public Integer viewsTotal = null;
    public Integer viewsWeekly = null;
    public Integer downloadsWeekly = null;
    public Integer downloadsTotal = null;
    private String createdAt;
    private String updatedAt = null;
    public int cardsCount;
    public UserSummaryDTO creator;

    // CLIENT-SIDE/SAVED DECKS ONLY
    public LearningDetailDTO learning;

    public GregorianCalendar getCreatedAt()
    {
        if (createdAt == null) return null;
        return DateTimeHelpers.fromDateOnlyToGregorianCalendar(createdAt);
    }
    public void setCreatedAt(GregorianCalendar createdAt)
    {
        if (createdAt == null)
            throw  new RuntimeException();
        this.createdAt = PRMApplication.GLOBAL_DATE_FORMAT.format(createdAt.getTime());
    }
    public GregorianCalendar getUpdatedAt()
    {
        if (updatedAt == null) return null;
        return DateTimeHelpers.fromDateOnlyToGregorianCalendar(updatedAt);
    }
    public void setUpdatedAt(GregorianCalendar updatedAt)
    {
        if (updatedAt == null)
        {
            this.updatedAt = null;
            return;
        }
        this.updatedAt = PRMApplication.GLOBAL_DATE_FORMAT.format(updatedAt.getTime());
    }
}
