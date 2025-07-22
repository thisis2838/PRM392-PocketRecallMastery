package com.prm392g2.prmapp.api;

import com.prm392g2.prmapp.dtos.users.ChangePasswordDTO;
import com.prm392g2.prmapp.dtos.users.EmailChangeDTO;
import com.prm392g2.prmapp.dtos.users.LoginRequestDTO;
import com.prm392g2.prmapp.dtos.users.LoginResponseDTO;
import com.prm392g2.prmapp.dtos.users.ResetPasswordDTO;
import com.prm392g2.prmapp.dtos.users.SendOtpRequestDTO;
import com.prm392g2.prmapp.dtos.users.UserSummaryDTO;
import com.prm392g2.prmapp.dtos.users.RegisterDTO;
import com.prm392g2.prmapp.dtos.users.VerifyOtpRequestDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface UserApi {
    @POST("api/users/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO request);

    @POST("api/users/pre-register")
    Call<Void> preRegister(@Body RegisterDTO dto);

    @POST("api/users/complete-registration")
    Call<Void> completeRegistration(@Body VerifyOtpRequestDTO request);

    @POST("api/users/request-email-change")
    Call<Void> requestEmailChange(@Header("Authorization") String token, @Body EmailChangeDTO dto);

    @POST("api/users/confirm-email-change")
    Call<Void> confirmEmailChange(@Header("Authorization") String token, @Body VerifyOtpRequestDTO request);

    @GET("api/users/me")
    Call<UserSummaryDTO> getCurrentUser();

    @POST("api/users/send-otp")
    Call<Void> sendOtp(@Body SendOtpRequestDTO request);

    @POST("api/users/confirm-reset-password")
    Call<Void> confirmResetPassword(@Body VerifyOtpRequestDTO request);

    @POST("api/users/request-reset-password")
    Call<Void> requestResetPassword(@Body ResetPasswordDTO dto);

    @POST("api/users/change-password")
    Call<Void> changePassword(@Header("Authorization") String token, @Body ChangePasswordDTO request);
}
