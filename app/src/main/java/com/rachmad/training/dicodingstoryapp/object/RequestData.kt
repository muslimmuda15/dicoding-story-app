package com.rachmad.training.dicodingstoryapp.`object`

import java.io.File

data class AuthRequestData(
    var name: String? = null,
    var email: String,
    var password: String
)

data class StoryRequestData(
    var description: String,
    var photo: File,
    var lat: Double?,
    var lon: Double?
)