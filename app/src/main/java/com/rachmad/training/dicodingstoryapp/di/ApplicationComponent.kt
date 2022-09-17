package com.rachmad.training.dicodingstoryapp.di

import com.rachmad.training.dicodingstoryapp.ui.RegisterActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface ApplicationComponent {

}