package com.prm392g2.prmapp.dtos.users;

public class ResetPasswordDTO {
    private String email;
    private String newPassword;

    public ResetPasswordDTO(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }
}
