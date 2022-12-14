package com.rachmad.training.dicodingstoryapp.di

import com.rachmad.training.dicodingstoryapp.repository.AuthRepository
import com.rachmad.training.dicodingstoryapp.repository.StoryRepository
import com.rachmad.training.dicodingstoryapp.sql.ApiDatabaseMediator
import com.rachmad.training.dicodingstoryapp.sql.StoryDatabase
import com.rachmad.training.dicodingstoryapp.sql.access.LoginAccess
import com.rachmad.training.dicodingstoryapp.sql.access.StoryAccess
import com.rachmad.training.dicodingstoryapp.ui.login.LoginActivity
import com.rachmad.training.dicodingstoryapp.ui.story.MainActivity
import com.rachmad.training.dicodingstoryapp.ui.story.add.CameraActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface ApplicationComponent {
    fun inject(authRepository: AuthRepository)
    fun inject(storyRepository: StoryRepository)
    fun inject(mainActivity: MainActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(cameraActivity: CameraActivity)
    fun inject(loginAccess: LoginAccess)
    fun inject(storyAccess: StoryAccess)
    fun inject(apiDatabaseMediator: ApiDatabaseMediator)
}