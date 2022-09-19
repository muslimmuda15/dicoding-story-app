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
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int = 0,
    ): Call<BaseResponseData>
}