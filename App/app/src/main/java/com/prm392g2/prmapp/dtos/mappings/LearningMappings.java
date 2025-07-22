package com.prm392g2.prmapp.dtos.mappings;

import com.prm392g2.prmapp.dtos.learnings.LearningDetailDTO;

public class LearningMappings
{
    public static LearningDetailDTO LearningToLearningDetailDTO(com.prm392g2.prmapp.database.entities.Learning learning)
    {
        if (learning == null)
            return null;

        var dto = new LearningDetailDTO();
        dto.id = learning.id;
        dto.currentCardIndex = learning.currentCardIndex;
        dto.seed = learning.seed;
        dto.lastLearnt = learning.lastLearnt;
        dto.hardCardIndexes = learning.hardCardIndexes;
        dto.inverted = learning.inverted;

        return dto;
    }
}
