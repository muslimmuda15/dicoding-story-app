package com.rachmad.training.dicodingstoryapp

import android.app.Application
import com.rachmad.training.dicodingstoryapp.di.AppModule
import com.rachmad.training.dicodingstoryapp.di.ApplicationComponent

class App: Application() {
    override fun onCreate() {
        super.onCreate()
//        appComponent = DaggerApplicationComponent.builder()
//            .appModule(AppModule(this))
    }

    companion object {
        lateinit var appComponent: ApplicationComponent
    }
}