package com.rachmad.training.dicodingstoryapp.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.io.Serializable

data class BaseResponseData(
    var error: Boolean?,
    var message: String?,
    var loginResult: LoginData? = null,
    var listStory: ArrayList<StoryData>? = null
)

@Entity(tableName = "account")
data class LoginData(
    @PrimaryKey
    @ColumnInfo(name = "user_id") var userId: String,
    var name: String?,
    var token: String?,
)

@Entity(
    tableName = "story",
    foreignKeys = arrayOf(ForeignKey(
        entity = LoginData::class,
        parentColumns = arrayOf("user_id"),
        childColumns = arrayOf("account_id"),
        onDelete = CASCADE,
        onUpdate = CASCADE
    ))
)
data class StoryData(
    @PrimaryKey
    @ColumnInfo(name = "story_id") var id: String,
    @ColumnInfo(name = "account_id") var accountId: String? = null,
    var name: String?,
    var description: String?,
    @ColumnInfo(name = "photo_url") var photoUrl: String?,
    @ColumnInfo(name = "created_at") var createdAt: String?,
    @ColumnInfo(name = "latitude") var lat: Double?,
    @ColumnInfo(name = "longitude") var lon: Double?
): Serializable