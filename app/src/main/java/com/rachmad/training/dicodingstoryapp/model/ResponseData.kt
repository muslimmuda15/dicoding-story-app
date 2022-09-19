package com.rachmad.training.dicodingstoryapp.model

data class BaseResponseData(
    var error: Boolean?,
    var message: String?,
    var loginResult: LoginData? = null,
    var listStory: ArrayList<StoryData>? = null
)

data class LoginData(
    var userId: String?,
    var name: String?,
    var token: String?
)

data class StoryData(
    var id: String,
    var name: String?,
    var description: String?,
    var photoUrl: String?,
    var createdAt: String?,
    var lat: Double?,
    var lon: Double?
)