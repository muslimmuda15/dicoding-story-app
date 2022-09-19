package com.rachmad.training.dicodingstoryapp.util

import android.util.Patterns
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rachmad.training.dicodingstoryapp.model.BaseResponseData
import okhttp3.ResponseBody

fun CharSequence?.isValidEmail() = !isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun errorResponseData(body: ResponseBody?): BaseResponseData {
    val type = object: TypeToken<BaseResponseData>() {}.type
    return Gson().fromJson(body?.charStream(), type)
}