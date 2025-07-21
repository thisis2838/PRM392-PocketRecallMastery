package com.prm392g2.prmapp.api;

import com.prm392g2.prmapp.dtos.users.LoginRequestDTO;
import com.prm392g2.prmapp.dtos.users.LoginResponseDTO;
import com.prm392g2.prmapp.dtos.users.UserSummaryDTO;
import com.prm392g2.prmapp.dtos.users.RegisterDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface UserApi {
    @POST("api/Users/login")
    Call<LoginResponseDTO> login(@Body LoginRequestDTO request);

    @POST("api/Users/register")
    Call<Void> register(@Body RegisterDTO dto);

    @GET("api/Users/me")
    Call<UserSummaryDTO> getCurrentUser();
}
