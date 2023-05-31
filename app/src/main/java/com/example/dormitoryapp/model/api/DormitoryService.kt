package com.example.dormitoryapp.model.api

import com.example.dormitoryapp.model.dto.LoginModel
import com.example.dormitoryapp.model.dto.ProfileModel
import com.example.dormitoryapp.model.dto.ResponseModel
import com.example.dormitoryapp.model.dto.SignInModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DormitoryService {

    @POST("authorization")
    suspend fun sendCode(@Body loginModel: LoginModel): Response<ResponseModel>

    @POST("authorization/login")
    suspend fun signIn(@Body signInModel: SignInModel): Response<ResponseModel>

    @POST("profile")
    suspend fun createProfile(@Body profileModel: ProfileModel): Response<ResponseModel>
}