package com.rachmad.training.dicodingstoryapp.webservice

import com.rachmad.training.dicodingstoryapp.model.LoginRequestData
import com.rachmad.training.dicodingstoryapp.model.BaseResponseData
import com.rachmad.training.dicodingstoryapp.model.CreateStoryRequestData
import retrofit2.Call
import retrofit2.http.*

interface EndPoint {
    @POST("register")
    fun register(
        @Body authData: LoginRequestData
    ): Call<BaseResponseData>

    @POST("login")
    fun login(
        @Body authData: LoginRequestData
    ): Call<BaseResponseData>

    @Headers("Content-Type: multipart/form-data")
    @POST("stories")
    fun addStories(
        @Header("Authorization") token: String,
        @Body storyData: CreateStoryRequestData
    ): Call<BaseResponseData>

    @POST("stories/guest")
    fun addGuestStories(
        @Body storyData: CreateStoryRequestData
    ): Call<BaseResponseData>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = 1,
        @Query("size") size: Int? = 50,
        @Query("location") location: Int = 0,
    ): Call<BaseResponseData>
}