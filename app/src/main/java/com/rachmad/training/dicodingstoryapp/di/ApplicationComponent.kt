package com.rachmad.training.dicodingstoryapp.di

import com.rachmad.training.dicodingstoryapp.repository.AuthRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface ApplicationComponent {
    fun inject(authRepository: AuthRepository)
}