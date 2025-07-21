package com.prm392g2.prmapp.dtos.users;

public class LoginRequestDTO
{
    private String userName;
    private String password;

    public LoginRequestDTO(String userName, String password)
    {
        this.userName = userName;
        this.password = password;
    }
}
