package com.example.dormitoryapp.model.api

import com.example.dormitoryapp.model.dto.*
import io.reactivex.rxjava3.core.Observable
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
    suspend fun getPostsData(@Header("Connection") connection: String = "close"): Response<List<PostModel>>

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

    @PUT("Post")
    suspend fun updatePost(
        @Body createPostModel: CreatePostModel,
        @Query("id") id: Int
    ): Response<ResponseModel>

    @GET("PostSubscription/{idProfile}")
    suspend fun getPostSubscriptionsByProfile(@Path("idProfile") idProfile: Int): Response<List<PostSubscriptionModel>>

    @POST("PostSubscription")
    suspend fun addPostSubscription(@Body createPostSubscriptionModel: CreatePostSubscriptionModel): Response<ResponseModel>

    @HTTP(method = "DELETE", path = "PostSubscription", hasBody = true)
    suspend fun deletePostSubscription(@Body createPostSubscriptionModel: CreatePostSubscriptionModel): Response<ResponseModel>


    @GET("ProfileSubscription/{idProfile}")
    suspend fun getProfileSubscriptionsByProfile(@Path("idProfile") idProfile: Int): Response<List<ProfileSubscriptionModel>>

    @POST("ProfileSubscription")
    suspend fun addProfileSubscription(@Body createProfeltSubscriptionModel: CreateProfileSubscriptionModel): Response<ResponseModel>

    @HTTP(method = "DELETE", path = "ProfileSubscription", hasBody = true)
    suspend fun deleteProfileSubscription(@Body createPostSubscriptionModel: CreateProfileSubscriptionModel): Response<ResponseModel>

    @POST("Notification?")
    suspend fun sendNotification(
        @Query("idNotifier") idNotifier: Int,
        @Query("idNotifying") idNotifying: Int,
        @Query("idPost") idPost: Int,
    ): Response<ResponseModel>

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GET("post")
    fun getPosts(): Observable<MutableList<PostModel>>

    @GET("post/{idPost}")
    fun getPostType(@Path("idPost", encoded = false) idPost: Int): Observable<PostTypeModel>

    @GET("poststypes")
    fun getCommentsOfPost(@Path("id", encoded = false) id: Int): Observable<List<PostTypeModel>>
}