package com.prm392g2.prmapp.dtos.users;

public class RegisterDto {
    public String username;
    public String email;
    public String password;

    public RegisterDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
