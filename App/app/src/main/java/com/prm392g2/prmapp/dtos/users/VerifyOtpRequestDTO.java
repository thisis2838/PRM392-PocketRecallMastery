package com.prm392g2.prmapp.dtos.users;

public class VerifyOtpRequestDTO {
    private String email;
    private String otp;
    private String purpose;

    public VerifyOtpRequestDTO(String email, String otp, String purpose) {
        this.email = email;
        this.otp = otp;
        this.purpose = purpose;
    }
}
