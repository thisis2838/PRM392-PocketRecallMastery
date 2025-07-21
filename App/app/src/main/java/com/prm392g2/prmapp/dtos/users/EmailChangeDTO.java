package com.prm392g2.prmapp.dtos.users;

public class EmailChangeDTO {
    private String newEmail;

    public EmailChangeDTO(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
