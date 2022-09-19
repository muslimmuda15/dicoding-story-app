package com.rachmad.training.dicodingstoryapp.di

import android.app.Application
import android.content.Context
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
}