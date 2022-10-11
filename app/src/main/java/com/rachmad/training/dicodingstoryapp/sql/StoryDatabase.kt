package com.rachmad.training.dicodingstoryapp.sql

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rachmad.training.dicodingstoryapp.App
import com.rachmad.training.dicodingstoryapp.BuildConfig
import com.rachmad.training.dicodingstoryapp.model.LoginData
import com.rachmad.training.dicodingstoryapp.model.RemoteKeys
import com.rachmad.training.dicodingstoryapp.model.StoryData
import com.rachmad.training.dicodingstoryapp.sql.query.LoginQuery
import com.rachmad.training.dicodingstoryapp.sql.query.RemoteKeyQuery
import com.rachmad.training.dicodingstoryapp.sql.query.StoryQuery
import javax.inject.Inject

@Database(entities = arrayOf(LoginData::class, StoryData::class, RemoteKeys::class), version = 1, exportSchema = false)
abstract class StoryDatabase: RoomDatabase() {
    abstract fun loginQuery(): LoginQuery
    abstract fun storyQuery(): StoryQuery
    abstract fun remoteKeyQuery(): RemoteKeyQuery
}