package com.prm392g2.prmapp.api;

import com.prm392g2.prmapp.dtos.users.LoginRequest;
import com.prm392g2.prmapp.dtos.users.LoginResponse;
import com.prm392g2.prmapp.dtos.users.UserDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface AuthApi {
    @POST("api/Users/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/users/me")
    Call<UserDto> getCurrentUser(@Header("Authorization") String bearerToken);
}
