package com.prm392g2.prmapp.dtos.users;

public class LoginRequest {
    private String userName;
    private String password;

    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
