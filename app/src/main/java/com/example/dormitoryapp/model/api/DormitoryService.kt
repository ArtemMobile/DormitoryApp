package com.example.dormitoryapp.model.api

import com.example.dormitoryapp.model.dto.*
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface DormitoryService {

    @POST("Authorization")
    suspend fun sendCode(@Body loginModel: LoginModel): Response<ResponseModel>

    @POST("Authorization/login")
    suspend fun signIn(@Body signInModel: SignInModel): Response<ProfileResponse>

    @POST("Authorization/register")
    suspend fun register(@Body signInModel: SignInModel): Response<ResponseModel>

    @POST("Profile")
    suspend fun createProfile(@Body profileModel: ProfileModel): Response<ProfileResponse>

    @PUT("Profile")
    suspend fun updateProfile(
        @Body profileModel: ProfileModel,
        @Query("id") id: Int
    ): Response<ProfileResponse>

    @GET("Post")
    suspend fun getPostsData( @Header("Connection") connection: String = "close"): Response<List<PostModel>>

    @GET("Post/byType")
    suspend fun getPostsByType(
        @Query("idType") idType: Int,
        @Header("Connection") connection: String = "close"
    ): Response<List<PostModel>>

    @GET("Profile/{id}")
    suspend fun getProfileById(@Path("id") id: Int): Response<ProfileModel>

    @GET("PostType")
    suspend fun getPostTypes(): Response<List<PostTypeModel>>

    @GET("Profile")
    suspend fun getAllUsers(): Response<List<ProfileModel>>

    @POST("Feedback")
    suspend fun sendFeedback(@Body feedbackModel: FeedbackModel): Response<ResponseModel>

    @POST("Post")
    suspend fun createPost(@Body createPostModel: CreatePostModel): Response<ResponseModel>

    @GET("post")
    fun getPosts(): Observable<MutableList<PostModel>>

    @GET("post/{idPost}")
    fun getPostType(@Path("idPost", encoded = false) idPost: Int): Observable<PostTypeModel>

    @GET("poststypes")
    fun getCommentsOfPost(@Path("id", encoded = false) id: Int): Observable<List<PostTypeModel>>
}