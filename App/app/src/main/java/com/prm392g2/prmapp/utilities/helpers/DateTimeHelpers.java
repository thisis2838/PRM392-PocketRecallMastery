package com.prm392g2.prmapp.utilities.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateTimeHelpers
{
    public static GregorianCalendar fromDateOnlyToGregorianCalendar(String dateOnlyString)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try
        {
            Date date = sdf.parse(dateOnlyString);
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
