package com.prm392g2.prmapp.dtos.users;

public class SendOtpRequestDTO {
    private String email;
    private String purpose;

    public SendOtpRequestDTO(String email, String purpose) {
        this.email = email;
        this.purpose = purpose;
    }
}
