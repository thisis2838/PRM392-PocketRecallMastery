package com.prm392g2.prmapp.dtos.mappings;

import com.prm392g2.prmapp.database.entities.User;
import com.prm392g2.prmapp.dtos.users.UserSummaryDTO;

public class UserMappings
{
    public static UserSummaryDTO UserToUserSummaryDTO(User user)
    {
        if (user == null)
            return null;

        UserSummaryDTO dto = new UserSummaryDTO();
        dto.id = user.id;
        dto.username = user.username;
        return dto;
    }

    public static User UserSummaryDTOToUser(UserSummaryDTO dto)
    {
        if (dto == null)
            return null;

        User user = new User();
        user.id = dto.id;
        user.username = dto.username;
        return user;
    }
}
