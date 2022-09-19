package com.rachmad.training.dicodingstoryapp

import android.app.Application
import com.rachmad.training.dicodingstoryapp.di.AppModule
import com.rachmad.training.dicodingstoryapp.di.ApplicationComponent
import com.rachmad.training.dicodingstoryapp.di.DaggerApplicationComponent

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerApplicationComponent.builder()
            .appModule(AppModule(this)).build()
    }

    companion object {
        lateinit var appComponent: ApplicationComponent
    }
}