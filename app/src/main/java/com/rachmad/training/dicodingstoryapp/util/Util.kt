package com.rachmad.training.dicodingstoryapp.util

import android.util.Log
import android.util.Patterns
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.model.BaseResponseData
import okhttp3.ResponseBody
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun CharSequence?.isValidEmail() = !isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun errorResponseData(body: ResponseBody?): BaseResponseData {
    val type = object: TypeToken<BaseResponseData>() {}.type
    return Gson().fromJson(body?.charStream(), type)
}

fun String.toDateLong(): Long {
    try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.parse(this)?.time ?: 0L
    } catch(e: Exception){
        return 0L
    }
}

fun Long.toTime(): String? {
    var convTime: String? = null
    val suffix = "ago"
    try {
        val pasTime = Date(this)
        val nowTime = Date()
        val dateDiff: Long = nowTime.getTime() - pasTime.getTime()
        val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
        val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
        val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
        val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
        if (second < 60) {
            if(second > 1)
                convTime = "$second secs $suffix"
            else
                convTime = "one sec $suffix"
        } else if (minute < 60) {
            if(minute > 1)
                convTime = "$minute mins $suffix"
            else
                convTime = "one min $suffix"
        } else if (hour < 24) {
            if(hour > 1)
                convTime = "$hour hours $suffix"
            else
                convTime = "one hour $suffix"
        } else if (day >= 7) {
            convTime = if (day > 360) {
                if((day / 360) > 1)
                    (day / 360).toString() + " years " + suffix
                else
                    "one year " + suffix
            } else if (day > 30) {
                if((day / 30) > 1)
                    (day / 30).toString() + " months " + suffix
                else
                    "one month " + suffix
            } else {
                if((day / 7) > 1)
                    (day / 7).toString() + " weeks " + suffix
                else
                    "one week " + suffix
            }
        } else if (day < 7) {
            if(day > 1)
                convTime = "$day days $suffix"
            else
                convTime = "one day $suffix"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        e.message?.let { Log.e("ConvTimeE", it) }
    }
    return convTime
}