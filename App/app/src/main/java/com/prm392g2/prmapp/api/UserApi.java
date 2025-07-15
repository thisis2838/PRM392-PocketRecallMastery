package com.prm392g2.prmapp.api;

import com.prm392g2.prmapp.dtos.users.LoginRequest;
import com.prm392g2.prmapp.dtos.users.LoginResponse;
import com.prm392g2.prmapp.dtos.users.UserDto;
import com.prm392g2.prmapp.dtos.users.RegisterDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface UserApi {
    @POST("api/Users/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/Users/register")
    Call<Void> register(@Body RegisterDto dto);

    @GET("api/Users/me")
    Call<UserDto> getCurrentUser(@Header("Authorization") String bearerToken);
}
