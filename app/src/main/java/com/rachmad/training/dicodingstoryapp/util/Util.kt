package com.rachmad.training.dicodingstoryapp.util

import android.content.Context
import android.util.Log
import android.util.Patterns
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.model.BaseResponseData
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.Exception

fun CharSequence?.isValidEmail() = !isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun errorResponseData(body: ResponseBody?): BaseResponseData {
    val type = object: TypeToken<BaseResponseData>() {}.type
    return Gson().fromJson(body?.charStream(), type)
}

class TimeUtil(val context: Context) {
    fun toDateLong(input: String): Long {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.parse(input)?.time ?: 0L
        } catch(e: Exception){
            return 0L
        }
    }

    fun toDescriptionDateTime(time: Long): String?{
        try {
            return SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(time)
        } catch (e: Exception) {
            return null
        }
    }

    fun toTime(time: Long): String? {
        var convTime: String? = null
        val suffix = context.getString(R.string.ago)
        try {
            val pasTime = Date(time)
            val nowTime = Date()
            val dateDiff: Long = nowTime.getTime() - pasTime.getTime()
            val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
            if (second < 60) {
                if(second > 1)
                    convTime = "$second ${context.getString(R.string.sec)} $suffix"
                else
                    convTime = "${context.getString(R.string.one_sec)} $suffix"
            } else if (minute < 60) {
                if(minute > 1)
                    convTime = "$minute ${context.getString(R.string.min)} $suffix"
                else
                    convTime = "${context.getString(R.string.one_min)} $suffix"
            } else if (hour < 24) {
                if(hour > 1)
                    convTime = "$hour ${context.getString(R.string.hour)} $suffix"
                else
                    convTime = "${context.getString(R.string.one_hour)} $suffix"
            } else if (day >= 7) {
                convTime = if (day > 360) {
                    if((day / 360) > 1)
                        "${(day / 360)} ${context.getString(R.string.year)} $suffix"
                    else
                        "${context.getString(R.string.one_year)} $suffix"
                } else if (day > 30) {
                    if((day / 30) > 1)
                        "${(day / 30)} ${context.getString(R.string.month)} $suffix"
                    else
                        "${context.getString(R.string.one_month)} $suffix"
                } else {
                    if((day / 7) > 1)
                        "${(day / 7)} ${context.getString(R.string.week)} $suffix"
                    else
                        "${context.getString(R.string.one_week)} $suffix"
                }
            } else if (day < 7) {
                if(day > 1)
                    convTime = "$day ${context.getString(R.string.day)} $suffix"
                else
                    convTime = "${context.getString(R.string.one_day)} $suffix"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convTime
    }
}