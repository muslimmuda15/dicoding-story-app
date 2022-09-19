package com.rachmad.training.dicodingstoryapp.model

import java.io.File

data class LoginRequestData(
    var name: String? = null,
    var email: String? = null,
    var password: String? = null
)

data class CreateStoryRequestData(
    var description: String,
    var photo: File,
    var lat: Double?,
    var lon: Double?
)