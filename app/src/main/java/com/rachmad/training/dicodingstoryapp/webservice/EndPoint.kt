package com.rachmad.training.dicodingstoryapp.webservice

import com.rachmad.training.dicodingstoryapp.`object`.AuthRequestData
import com.rachmad.training.dicodingstoryapp.`object`.BaseResponseData
import com.rachmad.training.dicodingstoryapp.`object`.StoryRequestData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface EndPoint {
    @POST("register")
    fun register(
        @Body authData: AuthRequestData
    ): Call<BaseResponseData>

    @POST("login")
    fun login(
        @Body authData: AuthRequestData
    ): Call<BaseResponseData>

    @POST("stories")
    fun addStories(
        @Body storyData: StoryRequestData
    ): Call<BaseResponseData>
}