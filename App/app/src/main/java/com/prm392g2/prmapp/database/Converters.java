package com.prm392g2.prmapp.database;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

public class Converters
{
    @TypeConverter
    public static List<Integer> fromString(String value)
    {
        if (value == null || value.isEmpty()) return null;
        return Arrays.stream(value.split(","))
            .map(Integer::parseInt)
            .collect(Collectors.toList());
    }

    @TypeConverter
    public static String fromList(List<Integer> list)
    {
        if (list == null) return null;
        return list.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));
    }

    @TypeConverter
    public static GregorianCalendar fromTimestamp(Long value)
    {
        if (value == null) return null;
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(value);
        return calendar;
    }

    @TypeConverter
    public static Long calendarToTimestamp(GregorianCalendar calendar)
    {
        return calendar == null ? null : calendar.getTimeInMillis();
    }
}