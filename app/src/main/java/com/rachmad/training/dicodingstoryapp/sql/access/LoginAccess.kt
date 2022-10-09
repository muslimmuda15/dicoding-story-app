package com.rachmad.training.dicodingstoryapp.sql.access

import androidx.lifecycle.LiveData
import com.rachmad.training.dicodingstoryapp.App
import com.rachmad.training.dicodingstoryapp.model.LoginData
import com.rachmad.training.dicodingstoryapp.sql.StoryDatabase
import javax.inject.Inject

class LoginAccess {
    @Inject lateinit var storyDatabase: StoryDatabase
    init {
        App.appComponent.inject(this)
    }

    fun insertLogin(loginData: LoginData) = storyDatabase.loginQuery().insertData(loginData)
    fun getAccountData(): LiveData<LoginData?> = storyDatabase.loginQuery().getAccountData()
    fun getToken(): String? = storyDatabase.loginQuery().getToken()
    fun deleteLogin() = storyDatabase.loginQuery().deleteData()
}