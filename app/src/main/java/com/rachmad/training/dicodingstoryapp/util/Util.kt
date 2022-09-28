package com.rachmad.training.dicodingstoryapp.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.net.Uri
import android.provider.MediaStore
import android.util.Patterns
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rachmad.training.dicodingstoryapp.R
import com.rachmad.training.dicodingstoryapp.model.BaseResponseData
import okhttp3.ResponseBody
import java.io.*
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


private const val cameraFileName = "story"

fun CharSequence?.isValidEmail() = !isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun errorResponseData(body: ResponseBody?): BaseResponseData {
    val type = object: TypeToken<BaseResponseData>() {}.type
    return Gson().fromJson(body?.charStream(), type)
}

fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T {
//    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//        activity.intent.getSerializableExtra(name, clazz)!!
//    else
        return activity.intent.getSerializableExtra(name) as T
}

fun rotateImage(bytes: ByteArray): ByteArray{
    /**
     * Beberapa hape merotate hasil gambarnya seperti samsung
     * maka dari itu perlu force image supaya posisinya selalu sesuai seperti take camera
     * untuk pengecekannya check perbandingan image width and heightnya
     * Note: belum melakukan advance research jenis hape apa aja yang merotate hasil gambarnya
     * jadi sementara melakukan proses ganda yang kurang efektif
     */
    var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    if(bitmap.width > bitmap.height) {
        val matrix = Matrix()
        matrix.postRotate(90F)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
    val b = byteArrayOutputStream.toByteArray()
    bitmap.recycle()
    return b
}

fun Image.save(context: Context, result: (Boolean) -> Unit) {
    try {
        val buffer: ByteBuffer = planes[0].buffer
        val bytes = ByteArray(buffer.capacity()).also { buffer.get(it) }
        val processedBytes = rotateImage(bytes)

        val cw = ContextWrapper(context)
        val dir = cw.getDir(cameraFileName, Context.MODE_PRIVATE)
        val file = File(dir, "image.jpg")
        /**
         * Penyimpanan di tempat tersembunyi
         */
        val fos = FileOutputStream(file)

        fos.write(processedBytes)
        fos.close()
        result(true)
    } catch(e: Exception){
        result(false)
    }
}

fun getPath(context: Context, uri: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor =
        context.getContentResolver().query(uri, projection, null, null, null) ?: return null
    val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor.moveToFirst()
    val s: String = cursor.getString(column_index)
    cursor.close()
    return s
}

fun loadFile(context: Context): File? {
    try {
        val cw = ContextWrapper(context)
        val dir = cw.getDir(cameraFileName, Context.MODE_PRIVATE)
        return File(dir, "image.jpg")
    } catch(e: Exception){
        return null
    }
}

fun loadImage(context: Context): Bitmap? {
    try {
        val cw = ContextWrapper(context)
        val dir = cw.getDir(cameraFileName, Context.MODE_PRIVATE)
        val file = File(dir, "image.jpg")

        val fis = FileInputStream(file)
        var bitmap = BitmapFactory.decodeStream(fis)

        val out = ByteArrayOutputStream()
        if(bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)){
            val bytes = out.toByteArray()
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
        out.close()
        fis.close()
        return bitmap
    } catch (e: Exception) {
        return null
    }
}

class TimeUtil(val context: Context) {
    fun toDateLong(input: String): Long {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.parse(input)?.time ?: 0L
        } catch(e: Exception){
            0L
        }
    }

    fun toDescriptionDateTime(time: Long): String?{
        return try {
            SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(time)
        } catch (e: Exception) {
            null
        }
    }

    fun toTime(time: Long): String? {
        var convTime: String? = null
        val suffix = context.getString(R.string.ago)
        try {
            val pasTime = Date(time)
            val nowTime = Date()
            val dateDiff: Long = nowTime.time - pasTime.time
            val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
            if (second < 60) {
                convTime = if(second > 1)
                    "$second ${context.getString(R.string.sec)} $suffix"
                else
                    "${context.getString(R.string.one_sec)} $suffix"
            } else if (minute < 60) {
                convTime = if(minute > 1)
                    "$minute ${context.getString(R.string.min)} $suffix"
                else
                    "${context.getString(R.string.one_min)} $suffix"
            } else if (hour < 24) {
                convTime = if(hour > 1)
                    "$hour ${context.getString(R.string.hour)} $suffix"
                else
                    "${context.getString(R.string.one_hour)} $suffix"
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
                convTime = if(day > 1)
                    "$day ${context.getString(R.string.day)} $suffix"
                else
                    "${context.getString(R.string.one_day)} $suffix"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return convTime
    }
}