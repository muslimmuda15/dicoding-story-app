package com.rachmad.training.dicodingstoryapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.rachmad.training.dicodingstoryapp.BuildConfig
import com.rachmad.training.dicodingstoryapp.util.Geolocation
import com.rachmad.training.dicodingstoryapp.util.TimeUtil
import com.rachmad.training.dicodingstoryapp.webservice.EndPoint
import com.rachmad.training.dicodingstoryapp.webservice.RetrofitBuilder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(val app: Application) {
    @Singleton
    @Provides
    fun provideContext(): Context = app.applicationContext

    @Singleton
    @Provides
    fun providesEndPoint(): EndPoint = RetrofitBuilder.build()

    @Singleton
    @Provides
    fun providesSharedPreferences(): SharedPreferences = provideContext().getSharedPreferences(BuildConfig.BUILD_TYPE + "_language", Context.MODE_PRIVATE)
}