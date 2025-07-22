package com.prm392g2.prmapp.dtos.learnings;

import java.util.GregorianCalendar;
import java.util.List;

public class LearningDetailDTO
{
    public int id;
    public int currentCardIndex;
    public int seed;
    public GregorianCalendar lastLearnt;
    public List<Integer> hardCardIndexes;
    public boolean inverted;
}
