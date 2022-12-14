package com.rachmad.training.dicodingstoryapp.webservice

import com.rachmad.training.dicodingstoryapp.model.LoginRequestData
import com.rachmad.training.dicodingstoryapp.model.BaseResponseData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

const val ITEM_SIZE = 20
interface EndPoint {
    @POST("register")
    fun register(
        @Body authData: LoginRequestData
    ): Call<BaseResponseData>

    @POST("login")
    fun login(
        @Body authData: LoginRequestData
    ): Call<BaseResponseData>

    @Multipart
    @POST("stories")
    fun addStories(
        @Header("Authorization") token: String,
        @Part("photo\"; filename=\"image") file: RequestBody,
        @Part("description") description: String,
        @Part("lat") lat: Double?,
        @Part("lon") long: Double?,
    ): Call<BaseResponseData>

    @Multipart
    @POST("stories/guest")
    fun addGuestStories(
        @Part("photo\"; filename=\"image") file: RequestBody,
        @Part("description") description: String,
        @Part("lat") lat: Double?,
        @Part("lon") long: Double?,
    ): Call<BaseResponseData>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = 1,
        @Query("size") size: Int? = ITEM_SIZE,
        @Query("location") location: Int = 0,
    ): BaseResponseData?
}