package com.example.dormitoryapp.model.api

import com.example.dormitoryapp.model.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface DormitoryService {

    @POST("authorization")
    suspend fun sendCode(@Body loginModel: LoginModel): Response<ResponseModel>

    @POST("authorization/login")
    suspend fun signIn(@Body signInModel: SignInModel): Response<ProfileResponse>

    @POST("authorization/register")
    suspend fun register(@Body signInModel: SignInModel): Response<ResponseModel>

    @POST("profile")
    suspend fun createProfile(@Body profileModel: ProfileModel): Response<ProfileResponse>

    @PUT("profile")
    suspend fun updateProfile(
        @Body profileModel: ProfileModel,
        @Query("id") id: Int
    ): Response<ProfileResponse>
}