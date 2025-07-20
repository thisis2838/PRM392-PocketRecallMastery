package com.prm392g2.prmapp.api;

import com.prm392g2.prmapp.dtos.users.LoginRequest;
import com.prm392g2.prmapp.dtos.users.LoginResponse;
import com.prm392g2.prmapp.dtos.users.ResetPasswordDTO;
import com.prm392g2.prmapp.dtos.users.SendOtpRequestDTO;
import com.prm392g2.prmapp.dtos.users.UserDto;
import com.prm392g2.prmapp.dtos.users.RegisterDto;
import com.prm392g2.prmapp.dtos.users.VerifyOtpRequestDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface UserApi {
    @POST("api/Users/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/users/pre-register")
    Call<Void> preRegister(@Body RegisterDto dto);

    @POST("api/users/complete-registration")
    Call<Void> completeRegistration(@Body VerifyOtpRequestDTO request);

    @GET("api/Users/me")
    Call<UserDto> getCurrentUser(@Header("Authorization") String bearerToken);

    @POST("api/users/send-otp")
    Call<Void> sendOtp(@Body SendOtpRequestDTO request);

    @POST("api/users/verify-otp")
    Call<Void> verifyOtp(@Body VerifyOtpRequestDTO request);

    @POST("api/users/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordDTO dto);
}
