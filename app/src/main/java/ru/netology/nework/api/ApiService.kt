package ru.netology.nework.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.nework.auth.AuthState
import ru.netology.nework.dto.*


interface ApiService {

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun onSignIn(
        @Field("login") login: String,
        @Field("password") pass: String
    ): Response<AuthState>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun onSignUpNoAva(
        @Field("login") login: String,
        @Field("password") pass: String,
        @Field("name") name: String,
        @Field("file") file: MultipartBody.Part?
    ): Response<AuthState>

    @Multipart
    @POST("users/registration")
    suspend fun onSignUpHasAva(
        @Part("login") login: RequestBody,
        @Part("password") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part file: MultipartBody.Part?
    ): Response<AuthState>

    @GET("users/{id}")
    suspend fun getUser(
        @Path("id") id: Int
    ): Response<UserRequest>

    @GET("users")
    suspend fun getUsers(): Response<List<UserRequest>>

    @Multipart
    @POST("media")
    suspend fun addMultimedia(@Part file: MultipartBody.Part?): Response<MediaResponse>

    @POST("posts")
    suspend fun addPost(@Body post: PostRequest): Response<PostResponse>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Int): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Int): Response<PostResponse>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Int): Response<PostResponse>

    @GET("posts/{post_id}")
    suspend fun getPost(@Path("post_id") id: Int): Response<PostResponse>

    @GET("posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<PostResponse>>

    @GET("posts/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<PostResponse>>

    @GET("posts/{id}/after")
    suspend fun getAfter(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<PostResponse>>

    @GET("posts")
    suspend fun getAll(): Response<List<PostResponse>>

    @GET("my/wall/latest/")
    suspend fun getPostMyWallLatest(@Query("count") count: Int): Response<List<PostResponse>>

    @GET("my/wall/{id}/before")
    suspend fun getPostMyWallBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<PostResponse>>

    @GET("my/wall/{id}/after")
    suspend fun getPostMyWallAfter(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<PostResponse>>

    @GET("{user_id}/jobs")
    suspend fun getUserJob(
        @Path("user_id") id: String
    ): Response<List<Job>>

    @POST("my/jobs")
    suspend fun addJob(@Body job: Job): Response<Job>

    @DELETE("my/jobs/{job_id}")
    suspend fun deleteJob(@Path("job_id") id: Int): Response<Unit>

    @GET("my/jobs")
    suspend fun getMyJob(): Response<MutableList<Job>>

    @GET("events/latest")
    suspend fun getLatestEvent(@Query("count") count: Int): Response<List<EventResponse>>

    @GET("events/{id}/before")
    suspend fun getBeforeEvent(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<EventResponse>>

    @GET("events/{id}/after")
    suspend fun getAfterEvent(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<EventResponse>>

    @DELETE("events/{id}")
    suspend fun removeByIdEvent(@Path("id") id: Int): Response<Unit>

    @POST("events/{id}/likes")
    suspend fun likeByIdEvent(@Path("id") id: Int): Response<EventResponse>

    @DELETE("events/{id}/likes")
    suspend fun dislikeByIdEvent(@Path("id") id: Int): Response<EventResponse>

    @POST("events/{id}/participants")
    suspend fun participateInEvent(@Path("id") id: Int): Response<EventResponse>

    @DELETE("events/{id}/participants")
    suspend fun doNotParticipateInEvent(@Path("id") id: Int): Response<EventResponse>

    @POST("events")
    suspend fun addEvent(@Body post: EventRequest): Response<EventResponse>

    @GET("events/{event_id}")
    suspend fun getEvent(@Path("event_id") id: Int): Response<EventResponse>
}